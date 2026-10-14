package pairro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import pairro.app.AppCoordinator;
import pairro.app.PeripheralControllers;
import pairro.audio.AudioController;
import pairro.clipboard.ClipboardController;
import pairro.device.DeviceDirectory;
import pairro.device.DeviceIdentity;
import pairro.device.DeviceState;
import pairro.input.KeyboardController;
import pairro.input.MouseController;
import pairro.network.NetworkManager;
import pairro.network.NetworkPairingListener;
import pairro.network.discovery.DeviceBroadcaster;
import pairro.network.discovery.DeviceScanner;
import pairro.network.handler.AudioHandler;
import pairro.network.handler.ClipboardHandler;
import pairro.network.handler.KeyboardInputHandler;
import pairro.network.handler.MouseInputHandler;
import pairro.network.lifecycle.ConnectionLifecycleManager;
import pairro.network.protocol.InboundMessageRouter;
import pairro.network.protocol.ProtocolMessenger;
import pairro.network.server.ClientHandler;
import pairro.network.session.UDPSessionState;
import pairro.pairing.PairingCoordinator;
import pairro.pairing.PairingService;
import pairro.store.PairedDeviceStore;
import pairro.store.SettingsStore;
import pairro.ui.component.common.ResizableCanvas;
import pairro.ui.component.common.TitleBar;
import pairro.ui.icon.IconLoader;
import pairro.ui.tray.TrayManager;
import pairro.ui.view.DashboardView;

public class App extends Application {
  private static final int TCP_PORT = 28401;
  private static final int UDP_PORT = 28402;
  private static final String DESKTOP_NAME = "DesktopServer";
  private static final String APP_TITLE = "Pairro";

  @Override
  public void start(Stage primaryStage) {
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setTitle(APP_TITLE);
    primaryStage.getIcons().addAll(loadIcons());

    SettingsStore settingsStore = new SettingsStore();
    TrayManager trayManager = new TrayManager(primaryStage, settingsStore);

    primaryStage.setOnCloseRequest(e -> {
      e.consume();
      trayManager.handleClose();
    });

    TitleBar titleBar = new TitleBar(primaryStage, APP_TITLE, trayManager::handleClose);

    AppCoordinator controller = createAppCoordinator();
    DashboardView dashboard = new DashboardView(primaryStage, controller, settingsStore);

    ResizableCanvas canvas = new ResizableCanvas();
    canvas.getStyleClass().add("white-canvas");

    StackPane canvasLayer = new StackPane(canvas, dashboard.getRoot());

    VBox.setVgrow(canvasLayer, Priority.ALWAYS);
    VBox.setVgrow(dashboard.getRoot(), Priority.ALWAYS);

    VBox root = new VBox(titleBar, canvasLayer);
    root.getStyleClass().add("app-shell");

    Scene scene = new Scene(root, 470, 700);
    String cssPath = getClass().getResource("/styles/styles.css").toExternalForm();
    scene.getStylesheets().add(cssPath);

    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private List<Image> loadIcons() {
    List<Image> icons = new ArrayList<>();

    for (int size : IconLoader.getIconSizes()) {
      try (InputStream inputStream = IconLoader.loadIconStream(size)) {
        if (inputStream != null) {
          icons.add(new Image(inputStream));
        }
      } catch (IOException exception) {
        System.err.println("[App] Could not load icon: " + exception.getMessage());
      }
    }

    return icons;
  }

  private AppCoordinator createAppCoordinator() {
    DeviceIdentity identity = new DeviceIdentity();
    PairedDeviceStore store = new PairedDeviceStore();
    PairingService pairingService = new PairingService(store);
    DeviceDirectory devices = new DeviceDirectory(new DeviceState(), pairingService);

    MouseController mouseController = PeripheralControllers.createMouseController();
    KeyboardController keyboardController = PeripheralControllers.createKeyboardController();
    AudioController audioController = PeripheralControllers.createAudioController();
    ClipboardController clipboardController = PeripheralControllers.createClipboardController();

    MouseInputHandler mouseInputHandler = new MouseInputHandler(mouseController);
    KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(keyboardController);
    AudioHandler audioHandler = new AudioHandler(audioController);
    ClipboardHandler clipboardHandler = new ClipboardHandler(clipboardController);

    ProtocolMessenger messenger = new ProtocolMessenger();
    InboundMessageRouter messageRouter = new InboundMessageRouter(
        mouseInputHandler,
        keyboardInputHandler,
        audioHandler,
        clipboardHandler,
        messenger);
    UDPSessionState udpSession = new UDPSessionState();

    NetworkManager networkManager = new NetworkManager(TCP_PORT, UDP_PORT, messageRouter, udpSession);
    PairingCoordinator pairing = new PairingCoordinator(
        pairingService,
        messenger,
        devices,
        networkManager,
        identity.getId(),
        DESKTOP_NAME);

    AppCoordinator controller = new AppCoordinator(pairing, devices);

    DeviceScanner scanner = new DeviceScanner(
        (id, name, address) -> {
          controller.onDeviceDiscovered(id, name, address);
        },
        identity.getId());

    DeviceBroadcaster broadcaster = new DeviceBroadcaster();
    ConnectionLifecycleManager connectionLifecycle = new ConnectionLifecycleManager(
        broadcaster,
        scanner,
        networkManager,
        devices,
        TCP_PORT,
        identity.getId());

    controller.setConnectionLifecycle(connectionLifecycle);

    networkManager.setPairingListener(new NetworkPairingListener() {
      @Override
      public void onPairRequest(
          String deviceId,
          String deviceName,
          String publicKey,
          ClientHandler client) {
        pairing.onPairRequest(deviceId, deviceName, publicKey, client);
      }

      @Override
      public void onAuth(
          String deviceId,
          String token,
          ClientHandler client) {
        networkManager.setTrustedUdpAddress(client.getAddress());
        pairing.onAuth(deviceId, token, client);
      }

      @Override
      public void onDisconnect(ClientHandler client) {
        pairing.onDisconnect(client);
      }
    });

    networkManager.setDataListener(controller::onDataReceived);

    return controller;
  }

  public static void main(String[] args) {
    System.setProperty("prism.text", "native");
    System.setProperty("prism.allowhidpi", "true");
    System.setProperty("prism.lcdtext", "false");
    launch(args);
  }
}
