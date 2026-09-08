package com.andinaseguros.entities.model;

import com.andinaseguros.entities.enums.RolUsuario;
import java.util.UUID;

public class Usuario {
    private final UUID id;
    private final String username;
    private final String passwordHash;
    private final RolUsuario rol;
    private final boolean activo;
    private final String provider;
    private final String providerUserId;
    private final String email;
    private final String facebookAccessToken;
    private final long facebookAccessTokenExpiresAt;
    private final String facebookScopes;

    public Usuario(UUID id, String username, String passwordHash, RolUsuario rol, boolean activo) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
        this.provider = null;
        this.providerUserId = null;
        this.email = username.contains("@") ? username : null;
        this.facebookAccessToken = null;
        this.facebookAccessTokenExpiresAt = 0;
        this.facebookScopes = null;
    }

    public Usuario(
            UUID id,
            String username,
            String passwordHash,
            RolUsuario rol,
            boolean activo,
            String provider,
            String providerUserId,
            String email,
            String facebookAccessToken,
            long facebookAccessTokenExpiresAt,
            String facebookScopes) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.facebookAccessToken = facebookAccessToken;
        this.facebookAccessTokenExpiresAt = facebookAccessTokenExpiresAt;
        this.facebookScopes = facebookScopes;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getProvider() { return provider; }
    public String getProviderUserId() { return providerUserId; }
    public String getEmail() { return email; }
    public String getFacebookAccessToken() { return facebookAccessToken; }
    public long getFacebookAccessTokenExpiresAt() { return facebookAccessTokenExpiresAt; }
    public String getFacebookScopes() { return facebookScopes; }

    public Usuario conAutorizacionFacebook(
            String encryptedAccessToken, long expiresAt, String scopes) {
        return new Usuario(
                id, username, passwordHash, rol, activo, provider, providerUserId, email,
                encryptedAccessToken, expiresAt, scopes);
    }

    public Usuario sinAutorizacionFacebook() {
        return new Usuario(id, username, passwordHash, rol, activo, null, null, email, null, 0, null);
    }
}
