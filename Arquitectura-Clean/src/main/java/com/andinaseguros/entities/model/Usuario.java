package com.andinaseguros.entities.model;

import com.andinaseguros.entities.enums.RolUsuario;
import java.util.UUID;

public class Usuario {
    private final UUID id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String googleSubject;
    private final RolUsuario rol;
    private final boolean activo;

    public Usuario(
            UUID id,
            String username,
            String email,
            String passwordHash,
            String googleSubject,
            RolUsuario rol,
            boolean activo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.googleSubject = googleSubject;
        this.rol = rol;
        this.activo = activo;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getGoogleSubject() {
        return googleSubject;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }
}
