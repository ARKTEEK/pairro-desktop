package vertexlink.ui.icon;

import java.io.InputStream;

public final class IconLoader {
  private static final int[] ICON_SIZES = {16, 32, 48, 64, 128, 256, 512};
  private static final String ICON_PATH = "/icons/icon-%d.png";

  public static int[] getIconSizes() {
    return ICON_SIZES;
  }

  public static InputStream loadIconStream(int size) {
    String path = String.format(ICON_PATH, size);

    return IconLoader.class.getResourceAsStream(path);
  }

  public static int findBestSize(int targetSize) {
    for (int size : ICON_SIZES) {
      if (size >= targetSize) {
        return size;
      }
    }

    return ICON_SIZES[ICON_SIZES.length - 1];
  }
}
