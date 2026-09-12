package com.andinaseguros.usecases.service.auth;

import com.andinaseguros.entities.enums.RolUsuario;
import com.andinaseguros.entities.exception.ReglaNegocioException;
import com.andinaseguros.entities.model.Usuario;
import com.andinaseguros.usecases.dto.Responses.TokenResponse;
import com.andinaseguros.usecases.port.out.facebook.FacebookOAuthPort;
import com.andinaseguros.usecases.port.out.facebook.OAuthStatePort;
import com.andinaseguros.usecases.port.out.id.IdGeneratorPort;
import com.andinaseguros.usecases.port.out.repository.UsuarioRepository;
import com.andinaseguros.usecases.port.out.security.AuthenticatedUser;
import com.andinaseguros.usecases.port.out.security.SecretEncryptionPort;
import com.andinaseguros.usecases.port.out.security.TokenGeneratorPort;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutenticarConFacebookUseCase {
    private static final Logger log = LoggerFactory.getLogger(AutenticarConFacebookUseCase.class);
    private static final String FACEBOOK = "FACEBOOK";
    // Algunos navegadores/proxies reenvían la misma redirección de OAuth casi al instante;
    // este cache evita que el reintento falle por el state de un solo uso ya consumido.
    private static final Duration VENTANA_DUPLICADOS = Duration.ofSeconds(30);
    private final ConcurrentHashMap<String, ResultadoCacheado> resultadosRecientes = new ConcurrentHashMap<>();
    private final OAuthStatePort states;
    private final FacebookOAuthPort facebook;
    private final UsuarioRepository usuarios;
    private final IdGeneratorPort ids;
    private final SecretEncryptionPort encryption;
    private final TokenGeneratorPort tokens;

    public AutenticarConFacebookUseCase(
            OAuthStatePort states, FacebookOAuthPort facebook, UsuarioRepository usuarios,
            IdGeneratorPort ids, SecretEncryptionPort encryption, TokenGeneratorPort tokens) {
        this.states = states;
        this.facebook = facebook;
        this.usuarios = usuarios;
        this.ids = ids;
        this.encryption = encryption;
        this.tokens = tokens;
    }

    public String iniciar() {
        String state = states.create();
        log.info("Facebook iniciar: statePrefix={}", state.substring(0, Math.min(8, state.length())));
        return facebook.authorizationUrl(state);
    }

    public TokenResponse callback(String code, String state) {
        purgarExpirados();
        if (state != null) {
            ResultadoCacheado cacheado = resultadosRecientes.get(state);
            if (cacheado != null && !cacheado.expirado()) {
                log.info("Facebook callback: reutilizando resultado cacheado statePrefix={}", prefijo(state));
                return cacheado.respuesta();
            }
        }
        boolean codeValido = code != null && !code.isBlank();
        boolean stateValido = codeValido && states.consume(state);
        log.info("Facebook callback: codePresente={} statePrefix={} stateConsumido={}",
                codeValido, prefijo(state), stateValido);
        if (!stateValido) {
            throw new ReglaNegocioException("FACEBOOK_CALLBACK_INVALIDO", "Respuesta de Facebook inválida");
        }
        var identity = facebook.exchangeCode(code);
        if (identity.id() == null || identity.id().isBlank()) {
            throw new ReglaNegocioException("FACEBOOK_IDENTIDAD_INVALIDA", "Facebook no devolvió una identidad válida");
        }
        if (identity.scopes() == null || identity.scopes().isEmpty()) {
            throw new ReglaNegocioException(
                    "FACEBOOK_PERMISOS_INSUFICIENTES",
                    "Facebook no concedió los permisos necesarios");
        }
        var usuario = usuarios.buscarPorProveedorYProveedorUsuarioId(FACEBOOK, identity.id())
            .map(existing -> actualizarAutorizacion(existing, identity))
            .orElseGet(() -> crearUsuario(identity));
        if (!usuario.isActivo()) {
            throw new ReglaNegocioException("CREDENCIALES_INVALIDAS", "Credenciales inválidas");
        }
        TokenResponse respuesta = new TokenResponse(tokens.generar(new AuthenticatedUser(usuario.getUsername(), usuario.getRol().name())),
                "Bearer", tokens.expirationSeconds());
        if (state != null) {
            resultadosRecientes.put(state, new ResultadoCacheado(respuesta, Instant.now().plus(VENTANA_DUPLICADOS)));
        }
        return respuesta;
    }

    private static String prefijo(String state) {
        return state == null ? "null" : state.substring(0, Math.min(8, state.length()));
    }

    private void purgarExpirados() {
        resultadosRecientes.values().removeIf(ResultadoCacheado::expirado);
    }

    private record ResultadoCacheado(TokenResponse respuesta, Instant expiraEn) {
        boolean expirado() {
            return Instant.now().isAfter(expiraEn);
        }
    }

    private Usuario crearUsuario(FacebookOAuthPort.FacebookIdentity identity) {
        String username = "facebook_" + identity.id();
        if (usuarios.buscarPorUsername(username).isPresent()) {
            throw new ReglaNegocioException("USUARIO_DUPLICADO", "No fue posible vincular la cuenta de Facebook");
        }
        if (identity.email() != null && usuarios.buscarPorUsername(identity.email()).isPresent()) {
            throw new ReglaNegocioException("VINCULACION_REQUIERE_CONFIRMACION", "La cuenta requiere vinculación confirmada");
        }
        var usuario =
            new Usuario(
                ids.generar(),
                username,
                identity.email(),
                null,
                null,
                RolUsuario.CLIENTE,
                true,
                null,
                false,
                FACEBOOK,
                identity.id(),
                encryption.encrypt(identity.accessToken()),
                identity.expiresAt() == null ? 0 : identity.expiresAt().getEpochSecond(),
                String.join(",", identity.scopes()))
                .conNombre(identity.firstName(), identity.lastName());
        return usuarios.guardar(usuario);
    }

    private Usuario actualizarAutorizacion(
            Usuario usuario, FacebookOAuthPort.FacebookIdentity identity) {
        var actualizado =
                identity.email() != null && !identity.email().isBlank()
                        ? usuario.conEmail(identity.email())
                        : usuario;
        actualizado =
                identity.firstName() != null && !identity.firstName().isBlank()
                        ? actualizado.conNombre(identity.firstName(), identity.lastName())
                        : actualizado;
        return usuarios.guardar(
                actualizado.conAutorizacionFacebook(
                        encryption.encrypt(identity.accessToken()),
                        identity.expiresAt() == null ? 0 : identity.expiresAt().getEpochSecond(),
                        String.join(",", identity.scopes())));
    }
}