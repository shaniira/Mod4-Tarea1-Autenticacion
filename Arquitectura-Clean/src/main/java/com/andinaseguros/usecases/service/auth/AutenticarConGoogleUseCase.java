package com.andinaseguros.usecases.service.auth;

import com.andinaseguros.entities.enums.RolUsuario;
import com.andinaseguros.entities.exception.ReglaNegocioException;
import com.andinaseguros.entities.model.Usuario;
import com.andinaseguros.usecases.dto.GoogleLoginRequestModel;
import com.andinaseguros.usecases.dto.Responses.TokenResponse;
import com.andinaseguros.usecases.port.out.id.IdGeneratorPort;
import com.andinaseguros.usecases.port.out.repository.ClienteRepository;
import com.andinaseguros.usecases.port.out.repository.UsuarioRepository;
import com.andinaseguros.usecases.port.out.security.*;

public class AutenticarConGoogleUseCase {
    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final GoogleIdentityVerifierPort googleIdentityVerifier;
    private final TokenGeneratorPort tokenGenerator;
    private final IdGeneratorPort ids;

    public AutenticarConGoogleUseCase(
            UsuarioRepository usuarios,
            ClienteRepository clientes,
            GoogleIdentityVerifierPort googleIdentityVerifier,
            TokenGeneratorPort tokenGenerator,
            IdGeneratorPort ids) {
        this.usuarios = usuarios;
        this.clientes = clientes;
        this.googleIdentityVerifier = googleIdentityVerifier;
        this.tokenGenerator = tokenGenerator;
        this.ids = ids;
    }

    public TokenResponse execute(GoogleLoginRequestModel solicitud) {
        GoogleIdentity identidad = verificar(solicitud.idToken());

        if (!identidad.emailVerified()) {
            throw emailNoVerificado();
        }

        Usuario usuario =
                usuarios.buscarPorGoogleSubject(identidad.subject())
                        .orElseGet(() -> vincularOCrearUsuario(identidad));

        if (!usuario.isActivo()) {
            throw usuarioInactivo();
        }

        var identity = new AuthenticatedUser(usuario.getUsername(), usuario.getRol().name());
        return new TokenResponse(
                tokenGenerator.generar(identity), "Bearer", tokenGenerator.expirationSeconds());
    }

    private GoogleIdentity verificar(String idToken) {
        try {
            return googleIdentityVerifier.verificar(idToken);
        } catch (RuntimeException e) {
            throw tokenInvalido();
        }
    }

    private Usuario vincularOCrearUsuario(GoogleIdentity identidad) {
        var existente = usuarios.buscarPorEmail(identidad.email());
        if (existente.isPresent()) {
            return vincularGoogleAUsuarioExistente(existente.get(), identidad.subject());
        }
        if (clientes.buscarPorCorreo(identidad.email()).isEmpty()) {
            throw clienteNoRegistrado();
        }
        return usuarios.guardar(
                new Usuario(
                        ids.generar(),
                        identidad.email(),
                        identidad.email(),
                        null,
                        identidad.subject(),
                        RolUsuario.CLIENTE,
                        true));
    }

    private Usuario vincularGoogleAUsuarioExistente(Usuario existente, String googleSubject) {
        return usuarios.guardar(
                new Usuario(
                        existente.getId(),
                        existente.getUsername(),
                        existente.getEmail(),
                        existente.getPasswordHash(),
                        googleSubject,
                        existente.getRol(),
                        existente.isActivo()));
    }

    private ReglaNegocioException tokenInvalido() {
        return new ReglaNegocioException(
                "GOOGLE_TOKEN_INVALIDO", "No se pudo validar el token de Google");
    }

    private ReglaNegocioException emailNoVerificado() {
        return new ReglaNegocioException(
                "GOOGLE_EMAIL_NO_VERIFICADO", "El correo de Google no está verificado");
    }

    private ReglaNegocioException usuarioInactivo() {
        return new ReglaNegocioException("USUARIO_INACTIVO", "El usuario está inactivo");
    }

    private ReglaNegocioException clienteNoRegistrado() {
        return new ReglaNegocioException(
                "CLIENTE_NO_REGISTRADO",
                "Tu correo no está registrado como cliente de Andina Seguros. Contacta a un agente"
                        + " para registrarte.");
    }
}
