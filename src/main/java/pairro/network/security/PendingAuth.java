package pairro.network.security;

public record PendingAuth(
    String deviceId,
    String clientNonce,
    String serverNonce,
    String certificateFingerprint) {}
