package pairro.notification;

import java.util.List;
import java.util.Objects;

import pairro.device.Device;
import pairro.network.server.ClientHandler;
import pairro.pairing.DashboardEventListener;

public class NotificationEventListener implements DashboardEventListener {
  private final DashboardEventListener delegate;
  private final NotificationService notificationService;
  private String connectedDeviceName;

  public NotificationEventListener(DashboardEventListener delegate, NotificationService notificationService) {
    this.delegate = delegate;
    this.notificationService = notificationService;
  }

  @Override
  public void onPairRequest(String deviceName, String addressKey, String calculatedPin, ClientHandler client,
      String deviceId) {
    delegate.onPairRequest(deviceName, addressKey, calculatedPin, client, deviceId);
    notificationService.publish(NotificationType.PAIRING_REQUEST, deviceName);
  }

  @Override
  public void onConnectionConflict(Device connectedDevice, String incomingDeviceName, String addressKey,
      String deviceId, ClientHandler client) {
    delegate.onConnectionConflict(connectedDevice, incomingDeviceName, addressKey, deviceId, client);
  }

  @Override
  public void onConnectedDeviceChanged(Device device) {
    delegate.onConnectedDeviceChanged(device);
    announceConnectionChange(device);
  }

  @Override
  public void onDeviceListUpdated(List<Device> devices) {
    delegate.onDeviceListUpdated(devices);
  }

  @Override
  public void onDataReceived(String data, String hostAddress) {
    delegate.onDataReceived(data, hostAddress);
  }

  private synchronized void announceConnectionChange(Device device) {
    String previousName = connectedDeviceName;
    String currentName = device == null ? null : device.getName();

    connectedDeviceName = currentName;

    if (Objects.equals(previousName, currentName)) {
      return;
    }

    if (previousName != null) {
      notificationService.publish(NotificationType.DEVICE_DISCONNECTED, previousName);
    }

    if (currentName != null) {
      notificationService.publish(NotificationType.DEVICE_CONNECTED, currentName);
    }
  }
}
