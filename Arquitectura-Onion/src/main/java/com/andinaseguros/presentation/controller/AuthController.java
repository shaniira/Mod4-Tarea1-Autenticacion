package com.andinaseguros.presentation.controller;

import com.andinaseguros.presentation.request.*;
import static com.andinaseguros.presentation.mapper.RestRequestMapper.toApplication;
import com.andinaseguros.application.dto.Responses.TokenResponse;
import com.andinaseguros.application.service.auth.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrarUsuarioUseCase registrarUsuario;
    private final AutenticarUsuarioUseCase autenticarUsuario;

    public AuthController(
            RegistrarUsuarioUseCase registrarUsuario, AutenticarUsuarioUseCase autenticarUsuario) {
        this.registrarUsuario = registrarUsuario;
        this.autenticarUsuario = autenticarUsuario;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CrearUsuarioRequest solicitud) {
        registrarUsuario.execute(toApplication(solicitud));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest solicitud) {
        return autenticarUsuario.execute(toApplication(solicitud));
    }
}
