package com.andinaseguros.core.domain.model;

import com.andinaseguros.core.domain.enums.RolUsuario;
import java.util.UUID;

public class Usuario {
    private final UUID id;
    private final String username;
    private final String passwordHash;
    private final RolUsuario rol;
    private final boolean activo;

    public Usuario(UUID id, String username, String passwordHash, RolUsuario rol, boolean activo) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
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
}
