package pairro.network.protocol;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import pairro.network.server.ClientHandler;

public class ProtocolMessenger {

  public void sendPairChallenge(ClientHandler client, String myId, String myName, String publicKey) {
    Map<String, String> fields = new LinkedHashMap<>();

    fields.put("deviceId", myId);
    fields.put("deviceName", myName);
    fields.put("publicKey", publicKey);

    client.send(Protocol.encode("PAIR_CHALLENGE", fields));
  }

  public void sendPairSuccess(ClientHandler client, String myId, String myName, String token) {
    Map<String, String> fields = new LinkedHashMap<>();

    fields.put("deviceId", myId);
    fields.put("deviceName", myName);
    fields.put("token", token);

    client.send(Protocol.encode("PAIR_SUCCESS", fields));
  }

  public void sendAuthResult(ClientHandler client, boolean ok, String reason) {
    Map<String, String> fields = new LinkedHashMap<>();

    if (!ok && reason != null) {
      fields.put("reason", reason);
    }

    client.send(Protocol.encode(ok ? "AUTH_OK" : "AUTH_FAIL", fields));
  }

  public void sendAuthChallenge(ClientHandler client, String serverNonce, String proof) {
    Map<String, String> fields = new LinkedHashMap<>();

    fields.put("nonce", serverNonce);
    fields.put("proof", proof);

    client.send(Protocol.encode("AUTH_CHALLENGE", fields));
  }

  public void sendPairDecision(ClientHandler client, boolean accepted, String reason) {
    Map<String, String> fields = new LinkedHashMap<>();

    fields.put("accepted", String.valueOf(accepted));

    if (!accepted && reason != null) {
      fields.put("reason", reason);
    }

    client.send(Protocol.encode("PAIR_DECISION", fields));
  }

  public void sendDisconnect(ClientHandler client) {
    client.send(Protocol.encode("DISCONNECT", Collections.emptyMap()));
  }

  public void sendPong(ClientHandler client) {
    client.send(Protocol.encode("PONG", Collections.emptyMap()));
  }
}
