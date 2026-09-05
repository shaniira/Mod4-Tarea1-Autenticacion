package com.andinaseguros.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.andinaseguros.presentation.request.CrearUsuarioRequest;
import com.andinaseguros.presentation.request.LoginRequest;
import com.andinaseguros.application.dto.CrearUsuarioDto;
import com.andinaseguros.application.dto.LoginDto;
import com.andinaseguros.application.dto.Responses.TokenResponse;
import com.andinaseguros.application.service.auth.*;
import com.andinaseguros.domain.enums.RolUsuario;
import org.junit.jupiter.api.Test;

class AuthControllerTest {
    @Test
    void controllerDelegaEnCasosDeUsoSinAccederAJpa() {
        var registro = mock(RegistrarUsuarioUseCase.class);
        var autenticacion = mock(AutenticarUsuarioUseCase.class);
        var controller = new AuthController(registro, autenticacion);
        var crear = new CrearUsuarioRequest("operador", "secreto", RolUsuario.ADMIN);
        var login = new LoginRequest("operador", "secreto");
        var crearCore = new CrearUsuarioDto("operador", "secreto", RolUsuario.ADMIN);
        var loginCore = new LoginDto("operador", "secreto");
        var token = new TokenResponse("jwt", "Bearer", 3600);
        when(autenticacion.execute(loginCore)).thenReturn(token);

        assertThat(controller.register(crear).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.login(login)).isEqualTo(token);
        verify(registro).execute(crearCore);
        verify(autenticacion).execute(loginCore);
    }
}
