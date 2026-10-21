package pairro.notification;

public enum NotificationType {
  PAIRING_REQUEST("Pairing request", "%s wants to pair with this PC"),
  DEVICE_CONNECTED("Device connected", "%s is now connected"),
  DEVICE_DISCONNECTED("Device disconnected", "%s has disconnected");

  private final String title;
  private final String messageFormat;

  NotificationType(String title, String messageFormat) {
    this.title = title;
    this.messageFormat = messageFormat;
  }

  public String title() {
    return title;
  }

  public String message(String deviceName) {
    return String.format(messageFormat, deviceName);
  }
}
