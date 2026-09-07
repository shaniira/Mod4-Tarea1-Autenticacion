package com.andinaseguros.interfaceadapters.in.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.andinaseguros.interfaceadapters.in.rest.request.CrearUsuarioRequest;
import com.andinaseguros.interfaceadapters.in.rest.request.GoogleLoginRequest;
import com.andinaseguros.interfaceadapters.in.rest.request.LoginRequest;
import com.andinaseguros.usecases.dto.CrearUsuarioRequestModel;
import com.andinaseguros.usecases.dto.GoogleLoginRequestModel;
import com.andinaseguros.usecases.dto.LoginRequestModel;
import com.andinaseguros.usecases.dto.Responses.TokenResponse;
import com.andinaseguros.usecases.service.auth.*;
import com.andinaseguros.entities.enums.RolUsuario;
import org.junit.jupiter.api.Test;

class AuthControllerTest {
    @Test
    void controllerDelegaEnCasosDeUsoSinAccederAJpa() {
        var registro = mock(RegistrarUsuarioUseCase.class);
        var autenticacion = mock(AutenticarUsuarioUseCase.class);
        var autenticacionGoogle = mock(AutenticarConGoogleUseCase.class);
        var controller = new AuthController(registro, autenticacion, autenticacionGoogle);
        var crear = new CrearUsuarioRequest("operador", "secreto", RolUsuario.ADMIN);
        var login = new LoginRequest("operador", "secreto");
        var crearCore = new CrearUsuarioRequestModel("operador", "secreto", RolUsuario.ADMIN);
        var loginCore = new LoginRequestModel("operador", "secreto");
        var token = new TokenResponse("jwt", "Bearer", 3600);
        when(autenticacion.execute(loginCore)).thenReturn(token);
        var google = new GoogleLoginRequest("id-token");
        var googleCore = new GoogleLoginRequestModel("id-token");
        var tokenGoogle = new TokenResponse("jwt-google", "Bearer", 3600);
        when(autenticacionGoogle.execute(googleCore)).thenReturn(tokenGoogle);

        assertThat(controller.register(crear).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.login(login)).isEqualTo(token);
        assertThat(controller.google(google)).isEqualTo(tokenGoogle);
        verify(registro).execute(crearCore);
        verify(autenticacion).execute(loginCore);
        verify(autenticacionGoogle).execute(googleCore);
    }
}
