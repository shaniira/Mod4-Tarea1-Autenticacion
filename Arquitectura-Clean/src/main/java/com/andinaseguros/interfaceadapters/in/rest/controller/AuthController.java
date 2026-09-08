package com.andinaseguros.interfaceadapters.in.rest.controller;

import com.andinaseguros.interfaceadapters.in.rest.request.*;
import static com.andinaseguros.interfaceadapters.in.rest.mapper.RestRequestMapper.toCore;
import com.andinaseguros.usecases.dto.Responses.TokenResponse;
import com.andinaseguros.usecases.dto.Responses.ResultadoLogin;
import com.andinaseguros.usecases.service.auth.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrarUsuarioUseCase registrarUsuario;
    private final AutenticarUsuarioUseCase autenticarUsuario;
    private final AutenticarConGoogleUseCase autenticarConGoogle;
    private final VerificarMfaUseCase verificarMfa;

    public AuthController(
            RegistrarUsuarioUseCase registrarUsuario,
            AutenticarUsuarioUseCase autenticarUsuario,
            AutenticarConGoogleUseCase autenticarConGoogle,
            VerificarMfaUseCase verificarMfa) {
        this.registrarUsuario = registrarUsuario;
        this.autenticarUsuario = autenticarUsuario;
        this.autenticarConGoogle = autenticarConGoogle;
        this.verificarMfa = verificarMfa;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CrearUsuarioRequest solicitud) {
        registrarUsuario.execute(toCore(solicitud));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResultadoLogin login(@Valid @RequestBody LoginRequest solicitud) {
        return autenticarUsuario.execute(toCore(solicitud));
    }

    @PostMapping("/google")
    public TokenResponse google(@Valid @RequestBody GoogleLoginRequest solicitud) {
        return autenticarConGoogle.execute(toCore(solicitud));
    }

    @PostMapping("/mfa/verificar")
    public TokenResponse verificarMfa(@Valid @RequestBody MfaVerifyRequest solicitud) {
        return verificarMfa.execute(toCore(solicitud));
    }
}
