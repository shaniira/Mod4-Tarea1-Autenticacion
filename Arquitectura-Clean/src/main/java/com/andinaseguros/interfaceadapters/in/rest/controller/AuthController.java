package com.andinaseguros.interfaceadapters.in.rest.controller;

import com.andinaseguros.interfaceadapters.in.rest.request.*;
import static com.andinaseguros.interfaceadapters.in.rest.mapper.RestRequestMapper.toCore;
import com.andinaseguros.usecases.dto.Responses.TokenResponse;
import com.andinaseguros.usecases.service.auth.*;
import com.andinaseguros.entities.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrarUsuarioUseCase registrarUsuario;
    private final AutenticarUsuarioUseCase autenticarUsuario;
    private final AutenticarConFacebookUseCase autenticarConFacebook;
    private final DesvincularFacebookUseCase desvincularFacebook;

    public AuthController(
            RegistrarUsuarioUseCase registrarUsuario,
            AutenticarUsuarioUseCase autenticarUsuario,
            AutenticarConFacebookUseCase autenticarConFacebook,
            DesvincularFacebookUseCase desvincularFacebook) {
        this.registrarUsuario = registrarUsuario;
        this.autenticarUsuario = autenticarUsuario;
        this.autenticarConFacebook = autenticarConFacebook;
        this.desvincularFacebook = desvincularFacebook;
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

    @GetMapping("/facebook")
    public ResponseEntity<Void> facebook() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(autenticarConFacebook.iniciar()))
                .build();
    }

    @GetMapping("/facebook/callback")
    public TokenResponse facebookCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        if (error != null) {
            throw new ReglaNegocioException("FACEBOOK_AUTORIZACION_RECHAZADA", "La autorización de Facebook fue rechazada");
        }
        return autenticarConFacebook.callback(code, state);
    }

    @DeleteMapping("/facebook")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> desvincularFacebook(Authentication authentication) {
        desvincularFacebook.execute(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
