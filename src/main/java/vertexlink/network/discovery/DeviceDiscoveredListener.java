package vertexlink.network.discovery;

public interface DeviceDiscoveredListener {
  void onDiscovered(String id, String name, String address);
}
