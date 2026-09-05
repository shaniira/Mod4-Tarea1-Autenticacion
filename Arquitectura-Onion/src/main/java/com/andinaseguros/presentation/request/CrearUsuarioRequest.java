package com.andinaseguros.presentation.request;

import com.andinaseguros.domain.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearUsuarioRequest(
        @NotBlank String username, @NotBlank String password, @NotNull RolUsuario rol) {}
