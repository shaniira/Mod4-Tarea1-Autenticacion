package com.andinaseguros.adapters.inbound.rest.controller;

import com.andinaseguros.core.ports.in.auth.RegistrarUsuarioUseCase;

import com.andinaseguros.core.ports.in.auth.AutenticarUsuarioUseCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.andinaseguros.adapters.inbound.rest.request.CrearUsuarioRequest;
import com.andinaseguros.adapters.inbound.rest.request.LoginRequest;
import com.andinaseguros.core.application.dto.CrearUsuarioCommand;
import com.andinaseguros.core.application.dto.LoginCommand;
import com.andinaseguros.core.application.dto.Responses.TokenResponse;
import com.andinaseguros.core.application.service.auth.*;
import com.andinaseguros.core.domain.enums.RolUsuario;
import org.junit.jupiter.api.Test;

class AuthControllerTest {
    @Test
    void controllerDelegaEnCasosDeUsoSinAccederAJpa() {
        var registro = mock(RegistrarUsuarioUseCase.class);
        var autenticacion = mock(AutenticarUsuarioUseCase.class);
        var controller = new AuthController(registro, autenticacion);
        var crear = new CrearUsuarioRequest("operador", "secreto", RolUsuario.ADMIN);
        var login = new LoginRequest("operador", "secreto");
        var crearCore = new CrearUsuarioCommand("operador", "secreto", RolUsuario.ADMIN);
        var loginCore = new LoginCommand("operador", "secreto");
        var token = new TokenResponse("jwt", "Bearer", 3600);
        when(autenticacion.execute(loginCore)).thenReturn(token);

        assertThat(controller.register(crear).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.login(login)).isEqualTo(token);
        verify(registro).execute(crearCore);
        verify(autenticacion).execute(loginCore);
    }
}
