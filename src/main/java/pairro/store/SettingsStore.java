package pairro.store;

public class SettingsStore extends BaseStore {
  private static final String CLOSE_TO_TRAY_KEY = "closeToTray";
  private static final String OPEN_ON_STARTUP_KEY = "openOnStartup";
  private static final String OPEN_IN_TRAY_KEY = "openInTray";
  private static final String DEVICE_NAME_KEY = "deviceName";
  private static final String AUTO_START_DISCOVERABILITY_KEY = "autoStartDiscoverability";
  private static final String SHOW_NOTIFICATIONS_KEY = "showNotifications";
  private static final String KEEP_PC_AWAKE_KEY = "keepPcAwake";
  private static final String CONFIRM_BEFORE_UNPAIRING_KEY = "confirmBeforeUnpairing";

  public SettingsStore() {
    super("settings.properties");
  }

  public synchronized boolean isCloseToTray() {
    return Boolean.parseBoolean(this.props.getProperty(CLOSE_TO_TRAY_KEY, "true"));
  }

  public synchronized void setCloseToTray(boolean value) {
    this.props.setProperty(CLOSE_TO_TRAY_KEY, String.valueOf(value));
    persist("pairro settings");
  }

  public synchronized boolean isOpenOnStartup() {
    return Boolean.parseBoolean(this.props.getProperty(OPEN_ON_STARTUP_KEY, "false"));
  }

  public synchronized void setOpenOnStartup(boolean value) {
    this.props.setProperty(OPEN_ON_STARTUP_KEY, String.valueOf(value));
    persist("pairro settings");
  }

  public synchronized boolean isOpenInTray() {
    return Boolean.parseBoolean(this.props.getProperty(OPEN_IN_TRAY_KEY, "false"));
  }

  public synchronized void setOpenInTray(boolean value) {
    this.props.setProperty(OPEN_IN_TRAY_KEY, String.valueOf(value));
    persist("pairro settings");
  }

  public synchronized String getDeviceName() {
    return this.props.getProperty(DEVICE_NAME_KEY, "Desktop");
  }

  public synchronized void setDeviceName(String value) {
    this.props.setProperty(DEVICE_NAME_KEY, value);
    persist("pairro settings");
  }

  public synchronized boolean isAutoStartDiscoverability() {
    return Boolean.parseBoolean(this.props.getProperty(AUTO_START_DISCOVERABILITY_KEY, "false"));
  }

  public synchronized void setAutoStartDiscoverability(boolean value) {
    this.props.setProperty(AUTO_START_DISCOVERABILITY_KEY, String.valueOf(value));
    persist("pairro settings");
  }

  public synchronized boolean isShowNotifications() {
    return Boolean.parseBoolean(this.props.getProperty(SHOW_NOTIFICATIONS_KEY, "true"));
  }

  public synchronized void setShowNotifications(boolean value) {
    this.props.setProperty(SHOW_NOTIFICATIONS_KEY, String.valueOf(value));
    persist("pairro settings");
  }

  public synchronized boolean isKeepPcAwake() {
    return Boolean.parseBoolean(this.props.getProperty(KEEP_PC_AWAKE_KEY, "false"));
  }

  public synchronized void setKeepPcAwake(boolean value) {
    this.props.setProperty(KEEP_PC_AWAKE_KEY, String.valueOf(value));
    persist("pairro settings");
  }

  public synchronized boolean isConfirmBeforeUnpairing() {
    return Boolean.parseBoolean(this.props.getProperty(CONFIRM_BEFORE_UNPAIRING_KEY, "true"));
  }

  public synchronized void setConfirmBeforeUnpairing(boolean value) {
    this.props.setProperty(CONFIRM_BEFORE_UNPAIRING_KEY, String.valueOf(value));
    persist("pairro settings");
  }
}
