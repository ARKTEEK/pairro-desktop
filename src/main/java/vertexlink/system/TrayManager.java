package vertexlink.system;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.stage.Stage;
import vertexlink.store.SettingsStore;

public class TrayManager {
  private static final String TOOLTIP = "VertexLink";

  private final Stage stage;
  private final SettingsStore settingsStore;
  private TrayIcon trayIcon;

  public TrayManager(Stage stage, SettingsStore settingsStore) {
    this.stage = stage;
    this.settingsStore = settingsStore;
  }

  public static boolean isSupported() {
    return SystemTray.isSupported();
  }

  public void handleClose() {
    if (settingsStore.isCloseToTray() && isSupported() && addTrayIcon()) {
      Platform.setImplicitExit(false);
      stage.hide();

      return;
    }

    removeTrayIcon();
    Platform.setImplicitExit(true);
    stage.close();
  }

  private void showWindow() {
    removeTrayIcon();

    stage.show();
    stage.setIconified(false);
    stage.toFront();
    stage.requestFocus();
  }

  private void quit() {
    removeTrayIcon();
    Platform.exit();
    System.exit(0);
  }

  private boolean addTrayIcon() {
    if (trayIcon != null) {
      return true;
    }

    MenuItem openItem = new MenuItem("Open VertexLink");
    openItem.addActionListener(e -> Platform.runLater(this::showWindow));

    MenuItem exitItem = new MenuItem("Exit");
    exitItem.addActionListener(e -> Platform.runLater(this::quit));

    PopupMenu menu = new PopupMenu();
    menu.add(openItem);
    menu.addSeparator();
    menu.add(exitItem);

    Image image = loadImage();

    if (image == null) {
      return false;
    }

    TrayIcon icon = new TrayIcon(image, TOOLTIP, menu);
    icon.setImageAutoSize(true);
    icon.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          Platform.runLater(TrayManager.this::showWindow);
        }
      }
    });

    try {
      SystemTray.getSystemTray().add(icon);
    } catch (AWTException | RuntimeException e) {
      System.err.println("[Tray] Could not add tray icon: " + e.getMessage());

      return false;
    }

    trayIcon = icon;

    return true;
  }

  private void removeTrayIcon() {
    if (trayIcon == null) {
      return;
    }

    SystemTray.getSystemTray().remove(trayIcon);
    trayIcon = null;
  }

  private Image loadImage() {
      Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
      int targetSize = Math.max(trayIconSize.width, trayIconSize.height);
      int bestSize = IconLoader.findBestSize(targetSize);

      return readImage(bestSize);
    }

    private Image readImage(int size) {
      try (InputStream inputStream = IconLoader.loadIconStream(size)) {
        if (inputStream == null) {
          System.err.println("[Tray] Icon not found for size: " + size);

          return null;
        }

        return ImageIO.read(inputStream);
      } catch (IOException exception) {
        System.err.println("[Tray] Could not read icon: " + exception.getMessage());

        return null;
      }
    }
}
