package com.andinaseguros.core.application.service.auth;

import com.andinaseguros.core.ports.in.auth.AutenticarUsuarioUseCase;

import com.andinaseguros.core.application.dto.LoginCommand;
import com.andinaseguros.core.application.dto.Responses.TokenResponse;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.ports.out.persistence.UsuarioRepositoryPort;
import com.andinaseguros.core.ports.out.security.*;

public class AutenticarUsuarioService implements AutenticarUsuarioUseCase {
    private final UsuarioRepositoryPort usuarios;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    public AutenticarUsuarioService(
            UsuarioRepositoryPort usuarios,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    public TokenResponse execute(LoginCommand solicitud) {
        var usuario =
                usuarios.buscarPorUsername(solicitud.username())
                        .orElseThrow(this::credencialesInvalidas);
        if (!usuario.isActivo()
                || !passwordEncoder.coincide(solicitud.password(), usuario.getPasswordHash())) {
            throw credencialesInvalidas();
        }
        var identity = new AuthenticatedUser(usuario.getUsername(), usuario.getRol().name());
        return new TokenResponse(
                tokenGenerator.generar(identity), "Bearer", tokenGenerator.expirationSeconds());
    }

    private ReglaNegocioException credencialesInvalidas() {
        return new ReglaNegocioException("CREDENCIALES_INVALIDAS", "Credenciales inválidas");
    }
}
