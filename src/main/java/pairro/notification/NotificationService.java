package pairro.notification;

import pairro.store.SettingsStore;

public class NotificationService {
  private final SettingsStore settingsStore;
  private final NotificationPresenter presenter;

  public NotificationService(SettingsStore settingsStore, NotificationPresenter presenter) {
    this.settingsStore = settingsStore;
    this.presenter = presenter;
  }

  public void publish(NotificationType type, String deviceName) {
    showNotification(type, deviceName);
  }

  private void showNotification(NotificationType type, String deviceName) {
    if (!settingsStore.isShowNotifications()) {
      return;
    }

    presenter.present(type, deviceName);
  }
}
