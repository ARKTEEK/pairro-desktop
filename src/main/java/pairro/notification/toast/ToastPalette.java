package pairro.notification.toast;

import javafx.scene.paint.Color;
import pairro.notification.NotificationType;

public final class ToastPalette {
  private static final Color MAGENTA = Color.web("#C2185B");
  private static final Color TEAL = Color.web("#2EC4B6");
  private static final Color MUTED = Color.web("#B08A9C");

  public static Color accentFor(NotificationType type) {
    return switch (type) {
      case PAIRING_REQUEST -> MAGENTA;
      case DEVICE_CONNECTED -> TEAL;
      case DEVICE_DISCONNECTED -> MUTED;
    };
  }
}
