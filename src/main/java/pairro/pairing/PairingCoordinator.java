package pairro.pairing;

import java.util.Optional;
import java.util.function.Supplier;

import pairro.device.Device;
import pairro.device.DeviceDirectory;
import pairro.network.NetworkManager;
import pairro.network.NetworkPairingListener;
import pairro.network.protocol.ProtocolMessenger;
import pairro.network.security.AuthHandshake;
import pairro.network.security.CryptoUtils;
import pairro.network.security.PendingAuth;
import pairro.network.server.ClientHandler;
import pairro.store.PairedDeviceStore;

public class PairingCoordinator implements NetworkPairingListener {
  private final PairingService pairingService;
  private final ProtocolMessenger messenger;
  private final DeviceDirectory devices;
  private final NetworkManager networkManager;
  private final String desktopId;
  private final Supplier<String> desktopName;

  private DashboardEventListener eventListener;

  public PairingCoordinator(
      PairingService pairingService,
      ProtocolMessenger messenger,
      DeviceDirectory devices,
      NetworkManager networkManager,
      String desktopId,
      Supplier<String> desktopName) {
    this.pairingService = pairingService;
    this.messenger = messenger;
    this.devices = devices;
    this.networkManager = networkManager;
    this.desktopId = desktopId;
    this.desktopName = desktopName;
  }

  public void setEventListener(DashboardEventListener eventListener) {
    this.eventListener = eventListener;
  }

  @Override
  public void onPairRequest(
      String deviceId,
      String deviceName,
      String clientPublicKeyStr,
      ClientHandler client) {
    String addressKey = client.getAddress().getHostAddress();
    PairingService.Challenge challenge = pairingService.createChallenge(addressKey, clientPublicKeyStr);

    messenger.sendPairChallenge(client, desktopId, desktopName.get(), challenge.desktopPublicKey);
    devices.addPendingClient(addressKey, client);

    if (eventListener != null) {
      eventListener.onPairRequest(deviceName, addressKey, challenge.pin, client, deviceId);
    }
  }

  @Override
  public void onAuthInit(String deviceId, String clientNonce, ClientHandler client) {
    Optional<PairedDeviceStore.PairedDevice> paired = deviceId == null
        ? Optional.empty()
        : pairingService.findPaired(deviceId);
    String certificateFingerprint = client.getCertificateFingerprint();

    if (paired.isEmpty() || !AuthHandshake.isValidNonce(clientNonce) || certificateFingerprint == null) {
      rejectAuth(client);

      return;
    }

    PendingAuth pending = new PendingAuth(
        deviceId,
        clientNonce,
        AuthHandshake.newNonce(),
        certificateFingerprint);

    client.beginAuth(pending);

    String serverProof = AuthHandshake.proof(paired.get().token(), AuthHandshake.ROLE_SERVER, pending);

    messenger.sendAuthChallenge(client, pending.serverNonce(), serverProof);
  }

  @Override
  public void onAuthProof(String clientProof, ClientHandler client) {
    PendingAuth pending = client.takePendingAuth();

    Optional<PairedDeviceStore.PairedDevice> paired = pending == null
        ? Optional.empty()
        : pairingService.findPaired(pending.deviceId());

    if (paired.isEmpty()) {
      rejectAuth(client);

      return;
    }

    String token = paired.get().token();
    String expectedProof = AuthHandshake.proof(token, AuthHandshake.ROLE_CLIENT, pending);

    if (!AuthHandshake.matches(expectedProof, clientProof)) {
      rejectAuth(client);

      return;
    }

    String deviceId = pending.deviceId();
    String addressKey = client.getAddress().getHostAddress();
    String deviceName = paired.get().deviceName();

    Device existingConnected = devices.getConnectedDevice();

    if (existingConnected == null || deviceId.equals(existingConnected.getClientId())) {
      completeConnection(client, addressKey, deviceId, deviceName, token);

      return;
    }

    devices.addPendingClient(addressKey, client);

    if (eventListener != null) {
      eventListener.onConnectionConflict(existingConnected, deviceName, addressKey, deviceId, client);
    }
  }

  private void rejectAuth(ClientHandler client) {
    messenger.sendAuthResult(client, false, "Authentication failed");
    client.close();
  }

  public void resolveConnectionConflict(
      ClientHandler client,
      String addressKey,
      String deviceId,
      String deviceName,
      boolean keepNew) {
    devices.removePendingClient(addressKey);

    if (keepNew) {
      Device previouslyConnected = devices.getConnectedDevice();

      if (previouslyConnected != null) {
        devices.disconnectClient(previouslyConnected.getClientId());
      }

      String token = pairingService.findPaired(deviceId).get().token();

      completeConnection(client, addressKey, deviceId, deviceName, token);
    } else {
      messenger.sendAuthResult(client, false, "Another device is already connected");
      client.close();
    }
  }

  public void handlePairingResponse(
      ClientHandler client,
      String addressKey,
      String deviceId,
      String deviceName,
      boolean accepted) {
    pairingService.discardChallenge(addressKey);

    if (accepted) {
      String token = pairingService.completePairing(deviceId, deviceName);

      byte[] sessionKey = CryptoUtils.deriveKeyFromToken(token);
      networkManager.setUdpSessionKey(sessionKey);
      networkManager.setTrustedUdpAddress(client.getAddress());

      client.markAuthenticated();
      messenger.sendPairSuccess(client, desktopId, desktopName.get(), token);
      devices.markPaired(addressKey, deviceName, deviceId);

      notifyDevicesChanged();
    } else {
      devices.removePendingClient(addressKey);
      messenger.sendPairDecision(client, false, "Rejected by user");

      client.close();
    }
  }

  @Override
  public void onDisconnect(ClientHandler client) {
    Device disconnected = devices.disconnectClientHandler(client);

    if (disconnected != null) {
      networkManager.clearTrustedUdpAddress();
      notifyDevicesChanged();
      notifyConnectedDeviceChanged();
    }
  }

  public void disconnectConnectedDevice() {
    Device connected = devices.getConnectedDevice();

    if (connected == null) {
      return;
    }

    ClientHandler client = devices.getConnectedClient(connected.getClientId());

    if (client != null) {
      messenger.sendDisconnect(client);
    }

    devices.disconnectClient(connected.getClientId());
    networkManager.clearTrustedUdpAddress();

    notifyDevicesChanged();
    notifyConnectedDeviceChanged();
  }

  private void completeConnection(
      ClientHandler client,
      String addressKey,
      String deviceId,
      String deviceName,
      String token) {
    byte[] sessionKey = CryptoUtils.deriveKeyFromToken(token);
    networkManager.setUdpSessionKey(sessionKey);
    networkManager.setTrustedUdpAddress(client.getAddress());

    client.markAuthenticated();
    messenger.sendAuthResult(client, true, null);
    devices.markConnected(addressKey, deviceName, deviceId, client);

    notifyDevicesChanged();
    notifyConnectedDeviceChanged();
  }

  private void notifyDevicesChanged() {
    if (eventListener != null) {
      eventListener.onDeviceListUpdated(devices.getDevicesList());
    }
  }

  private void notifyConnectedDeviceChanged() {
    if (eventListener != null) {
      eventListener.onConnectedDeviceChanged(devices.getConnectedDevice());
    }
  }
}
