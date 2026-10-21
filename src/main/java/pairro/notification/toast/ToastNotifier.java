package pairro.notification.toast;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import pairro.notification.NotificationPresenter;
import pairro.notification.NotificationType;

public class ToastNotifier implements NotificationPresenter {
  private static final int MAX_VISIBLE = 4;
  private static final double MARGIN = 16;
  private static final double GAP = 10;

  private final Runnable onClick;
  private final Image icon = ToastIcon.load();
  private final List<ToastStage> visibleToasts = new ArrayList<>();

  public ToastNotifier(Runnable onClick) {
    this.onClick = onClick;
  }

  @Override
  public void present(NotificationType type, String deviceName) {
    Platform.runLater(() -> show(type, deviceName));
  }

  private void show(NotificationType type, String deviceName) {
    dismissOldest();

    ToastContent content = new ToastContent(type.title(), type.message(deviceName), ToastPalette.accentFor(type));
    ToastStage toast = new ToastStage(content, icon, onClick, this::remove);

    visibleToasts.add(toast);
    restack();
    toast.display();
  }

  private void dismissOldest() {
    while (visibleToasts.size() >= MAX_VISIBLE) {
      visibleToasts.remove(0).dismiss();
    }
  }

  private void remove(ToastStage toast) {
    visibleToasts.remove(toast);
    restack();
  }

  private void restack() {
    Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    double x = bounds.getMaxX() - ToastPane.WIDTH - MARGIN;
    double y = bounds.getMaxY() - MARGIN;

    for (int index = visibleToasts.size() - 1; index >= 0; index--) {
      y -= ToastPane.HEIGHT;
      visibleToasts.get(index).moveTo(x, y);
      y -= GAP;
    }
  }
}
