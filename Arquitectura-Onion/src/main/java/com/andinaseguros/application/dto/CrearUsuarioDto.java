package com.andinaseguros.application.dto;

import com.andinaseguros.domain.enums.RolUsuario;

public record CrearUsuarioDto(String username, String password, RolUsuario rol) {}
