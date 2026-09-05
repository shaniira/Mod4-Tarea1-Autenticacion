package com.andinaseguros.adapters.inbound.rest.request;

import com.andinaseguros.core.domain.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearUsuarioRequest(
        @NotBlank String username, @NotBlank String password, @NotNull RolUsuario rol) {}
