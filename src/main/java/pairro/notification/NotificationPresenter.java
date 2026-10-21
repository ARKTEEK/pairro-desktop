package pairro.notification;

public interface NotificationPresenter {
  void present(NotificationType type, String deviceName);
}
