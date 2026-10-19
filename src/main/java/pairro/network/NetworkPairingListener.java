package pairro.network;

import pairro.network.server.ClientHandler;

public interface NetworkPairingListener {
  void onPairRequest(String deviceId, String deviceName, String publicKey, ClientHandler client);

  void onAuthInit(String deviceId, String clientNonce, ClientHandler client);

  void onAuthProof(String proof, ClientHandler client);

  void onDisconnect(ClientHandler client);
}
