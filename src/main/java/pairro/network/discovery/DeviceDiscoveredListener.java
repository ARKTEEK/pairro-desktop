package pairro.network.discovery;

public interface DeviceDiscoveredListener {
  void onDiscovered(String id, String name, String address);
}
