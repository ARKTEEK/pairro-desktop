package pairro.device;

import java.nio.charset.StandardCharsets;

public final class DeviceName {
  public static final String DEFAULT = "Desktop";
  private static final int MAX_BYTE = 63;

  public static String normalize(String raw) {
    String name = raw == null ? "" : raw.trim().replaceAll("\\s+", " ");

    while (name.getBytes(StandardCharsets.UTF_8).length > MAX_BYTE) {
      name = name.substring(0, name.offsetByCodePoints(name.length(), -1)).trim();
    }

    return name.isEmpty() ? DEFAULT : name;
  }
}
