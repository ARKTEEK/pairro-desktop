package pairro.network.lifecycle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import pairro.device.DeviceDirectory;
import pairro.network.NetworkManager;
import pairro.network.discovery.DeviceBroadcaster;
import pairro.network.discovery.DeviceScanner;

public class ConnectionLifecycleManager {
  private final DeviceBroadcaster broadcaster;
  private final DeviceScanner scanner;
  private final NetworkManager networkManager;
  private final DeviceDirectory devices;
  private final int tcpPort;
  private final String desktopId;
  private final Supplier<String> deviceName;

  private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
    Thread thread = new Thread(r, "network-lifecycle");
    thread.setDaemon(true);

    return thread;
  });

  private volatile boolean connected = false;
  private volatile boolean transitioning = false;

  private Consumer<Boolean> connectionStateListener;
  private Consumer<Boolean> connectionTransitionListener;

  public ConnectionLifecycleManager(
      DeviceBroadcaster broadcaster,
      DeviceScanner scanner,
      NetworkManager networkManager,
      DeviceDirectory devices,
      int tcpPort,
      String desktopId,
      Supplier<String> deviceName) {
    this.broadcaster = broadcaster;
    this.scanner = scanner;
    this.networkManager = networkManager;
    this.devices = devices;
    this.tcpPort = tcpPort;
    this.desktopId = desktopId;
    this.deviceName = deviceName;
  }

  public void setConnectionStateListener(Consumer<Boolean> listener) {
    this.connectionStateListener = listener;
  }

  public void setConnectionTransitionListener(Consumer<Boolean> listener) {
    this.connectionTransitionListener = listener;
  }

  public boolean isConnected() {
    return connected;
  }

  public void toggle() {
    if (transitioning) {
      return;
    }

    transitioning = true;

    boolean goingOnline = !connected;

    notifyTransitionStarted(goingOnline);

    executor.submit(() -> {
      try {
        if (goingOnline) {
          broadcaster.start(deviceName.get(), tcpPort, desktopId);
          scanner.start();
          networkManager.start();
        } else {
          scanner.stop();
          broadcaster.stop();
          networkManager.stop();
          devices.clear();
        }

        connected = goingOnline;
      } catch (Exception e) {
        System.err.println("[Dashboard] Failed to toggle connection: " + e.getMessage());

        connected = !goingOnline;
      } finally {
        transitioning = false;

        notifyStateChanged();
      }
    });
  }

  public void rename() {
    executor.submit(() -> {
      if (!connected) {
        return;
      }

      broadcaster.stop();
      broadcaster.start(deviceName.get(), tcpPort, desktopId);
    });
  }

  public void refreshDevices() {
    executor.submit(() -> {
      scanner.stop();
      scanner.start();
    });
  }

  public void shutdown() {
    executor.shutdown();
  }

  private void notifyTransitionStarted(boolean goingOnline) {
    if (connectionTransitionListener == null) {
      return;
    }

    if (Platform.isFxApplicationThread()) {
      connectionTransitionListener.accept(goingOnline);
    } else {
      Platform.runLater(() -> connectionTransitionListener.accept(goingOnline));
    }
  }

  private void notifyStateChanged() {
    Platform.runLater(() -> {
      if (connectionStateListener != null) {
        connectionStateListener.accept(connected);
      }
    });
  }
}
