package vertexlink.network;

import vertexlink.network.server.ClientHandler;

public interface NetworkDataListener {
  void onData(String data, ClientHandler client);
}
