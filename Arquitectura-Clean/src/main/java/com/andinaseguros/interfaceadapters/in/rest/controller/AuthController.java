package com.andinaseguros.interfaceadapters.in.rest.controller;

import com.andinaseguros.interfaceadapters.in.rest.request.*;
import static com.andinaseguros.interfaceadapters.in.rest.mapper.RestRequestMapper.toCore;
import com.andinaseguros.usecases.dto.Responses.TokenResponse;
import com.andinaseguros.usecases.service.auth.*;
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
