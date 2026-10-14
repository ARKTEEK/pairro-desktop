package pairro.store;

import java.util.Optional;

public class PairedDeviceStore extends BaseStore {
  public record PairedDevice(String deviceId, String deviceName, String token) {
  }

  public PairedDeviceStore() {
    super("paired_devices.properties");
  }

  public synchronized void save(String deviceId, String deviceName, String token) {
    this.props.setProperty(deviceId + ".name", deviceName);
    this.props.setProperty(deviceId + ".token", token);

    persist("pairro paired devices");
  }

  public synchronized Optional<PairedDevice> find(String deviceId) {
    String name = this.props.getProperty(deviceId + ".name");
    String token = this.props.getProperty(deviceId + ".token");

    if (name == null || token == null) {
      return Optional.empty();
    }

    return Optional.of(new PairedDevice(deviceId, name, token));
  }

  public synchronized void remove(String deviceId) {
    this.props.remove(deviceId + ".name");
    this.props.remove(deviceId + ".token");

    persist("pairro paired devices");
  }
}
