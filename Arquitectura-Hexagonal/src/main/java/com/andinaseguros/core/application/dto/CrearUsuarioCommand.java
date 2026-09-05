package com.andinaseguros.core.application.dto;

import com.andinaseguros.core.domain.enums.RolUsuario;

public record CrearUsuarioCommand(String username, String password, RolUsuario rol) {}
