package com.andinaseguros.adapters.inbound.rest.controller;

import com.andinaseguros.core.ports.in.auth.RegistrarUsuarioUseCase;

import com.andinaseguros.core.ports.in.auth.AutenticarUsuarioUseCase;

import com.andinaseguros.adapters.inbound.rest.request.*;
import static com.andinaseguros.adapters.inbound.rest.mapper.RestRequestMapper.toCore;
import com.andinaseguros.core.application.dto.Responses.TokenResponse;
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
        registrarUsuario.execute(toCore(solicitud));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest solicitud) {
        return autenticarUsuario.execute(toCore(solicitud));
    }
}
