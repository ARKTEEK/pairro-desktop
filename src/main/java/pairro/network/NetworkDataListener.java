package pairro.network;

import pairro.network.server.ClientHandler;

public interface NetworkDataListener {
  void onData(String data, ClientHandler client);
}
