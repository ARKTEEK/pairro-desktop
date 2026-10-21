package pairro.notification.toast;

import java.io.InputStream;
import javafx.scene.image.Image;
import pairro.ui.icon.IconLoader;

public final class ToastIcon {
  private static final int TARGET_SIZE = 64;

  public static Image load() {
    int size = IconLoader.findBestSize(TARGET_SIZE);

    try (InputStream stream = IconLoader.loadIconStream(size)) {
      return stream == null ? null : new Image(stream);
    } catch (Exception exception) {
      System.err.println("[Toast] Could not load icon: " + exception.getMessage());

      return null;
    }
  }
}
