package com.andinaseguros.usecases.service.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andinaseguros.entities.enums.RolUsuario;
import com.andinaseguros.entities.exception.ReglaNegocioException;
import com.andinaseguros.entities.model.Usuario;
import com.andinaseguros.usecases.dto.GoogleLoginRequestModel;
import com.andinaseguros.usecases.port.out.id.IdGeneratorPort;
import com.andinaseguros.usecases.port.out.repository.UsuarioRepository;
import com.andinaseguros.usecases.port.out.security.AuthenticatedUser;
import com.andinaseguros.usecases.port.out.security.GoogleIdentity;
import com.andinaseguros.usecases.port.out.security.GoogleIdentityVerifierPort;
import com.andinaseguros.usecases.port.out.security.TokenGeneratorPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AutenticarConGoogleUseCaseTest {
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final GoogleIdentityVerifierPort verifier = mock(GoogleIdentityVerifierPort.class);
    private final TokenGeneratorPort tokenGenerator = mock(TokenGeneratorPort.class);
    private final IdGeneratorPort ids = mock(IdGeneratorPort.class);
    private final AutenticarConGoogleUseCase useCase =
            new AutenticarConGoogleUseCase(usuarios, verifier, tokenGenerator, ids);
    private final GoogleLoginRequestModel solicitud = new GoogleLoginRequestModel("id-token");

    private GoogleIdentity identidad(String subject, String email, boolean emailVerified) {
        return new GoogleIdentity(subject, email, emailVerified, "Usuario Demo", "https://pic");
    }

    @Test
    void rechazaUnTokenDeGoogleInvalido() {
        when(verifier.verificar("id-token")).thenThrow(new RuntimeException("firma inválida"));

        assertThatThrownBy(() -> useCase.execute(solicitud))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo("GOOGLE_TOKEN_INVALIDO");

        verifyNoInteractions(usuarios, tokenGenerator);
    }

    @Test
    void rechazaUnEmailDeGoogleNoVerificado() {
        when(verifier.verificar("id-token")).thenReturn(identidad("sub-1", "a@x.com", false));

        assertThatThrownBy(() -> useCase.execute(solicitud))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo("GOOGLE_EMAIL_NO_VERIFICADO");

        verifyNoInteractions(usuarios, tokenGenerator);
    }

    @Test
    void autenticaUnUsuarioGoogleExistenteYActivo() {
        var existente =
                new Usuario(
                        UUID.randomUUID(),
                        "a@x.com",
                        "a@x.com",
                        null,
                        "sub-1",
                        RolUsuario.CLIENTE,
                        true);
        when(verifier.verificar("id-token")).thenReturn(identidad("sub-1", "a@x.com", true));
        when(usuarios.buscarPorGoogleSubject("sub-1")).thenReturn(Optional.of(existente));
        when(tokenGenerator.generar(new AuthenticatedUser("a@x.com", "CLIENTE")))
                .thenReturn("jwt-andina");
        when(tokenGenerator.expirationSeconds()).thenReturn(28800L);

        var respuesta = useCase.execute(solicitud);

        assertThat(respuesta.token()).isEqualTo("jwt-andina");
        assertThat(respuesta.tipo()).isEqualTo("Bearer");
        assertThat(respuesta.expiraEnSegundos()).isEqualTo(28800L);
        verify(usuarios, never()).guardar(any());
    }

    @Test
    void rechazaUnUsuarioGoogleExistenteInactivo() {
        var inactivo =
                new Usuario(
                        UUID.randomUUID(),
                        "a@x.com",
                        "a@x.com",
                        null,
                        "sub-1",
                        RolUsuario.CLIENTE,
                        false);
        when(verifier.verificar("id-token")).thenReturn(identidad("sub-1", "a@x.com", true));
        when(usuarios.buscarPorGoogleSubject("sub-1")).thenReturn(Optional.of(inactivo));

        assertThatThrownBy(() -> useCase.execute(solicitud))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo("USUARIO_INACTIVO");

        verifyNoInteractions(tokenGenerator);
    }

    @Test
    void creaUnUsuarioNuevoConRolClienteCuandoNoExisteNiPorSubNiPorEmail() {
        var nuevoId = UUID.randomUUID();
        when(verifier.verificar("id-token")).thenReturn(identidad("sub-nuevo", "nuevo@x.com", true));
        when(usuarios.buscarPorGoogleSubject("sub-nuevo")).thenReturn(Optional.empty());
        when(usuarios.buscarPorEmail("nuevo@x.com")).thenReturn(Optional.empty());
        when(ids.generar()).thenReturn(nuevoId);
        when(usuarios.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenGenerator.generar(any())).thenReturn("jwt-andina");
        when(tokenGenerator.expirationSeconds()).thenReturn(28800L);

        var respuesta = useCase.execute(solicitud);

        assertThat(respuesta.token()).isEqualTo("jwt-andina");
        var captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).guardar(captor.capture());
        var creado = captor.getValue();
        assertThat(creado.getId()).isEqualTo(nuevoId);
        assertThat(creado.getUsername()).isEqualTo("nuevo@x.com");
        assertThat(creado.getEmail()).isEqualTo("nuevo@x.com");
        assertThat(creado.getGoogleSubject()).isEqualTo("sub-nuevo");
        assertThat(creado.getPasswordHash()).isNull();
        assertThat(creado.getRol()).isEqualTo(RolUsuario.CLIENTE);
        assertThat(creado.isActivo()).isTrue();
    }

    @Test
    void rechazaCuandoYaExisteUnaCuentaLocalConElMismoEmailSinGoogleVinculado() {
        var cuentaLocal =
                new Usuario(
                        UUID.randomUUID(),
                        "a@x.com",
                        "a@x.com",
                        "hash-bcrypt",
                        null,
                        RolUsuario.CLIENTE,
                        true);
        when(verifier.verificar("id-token")).thenReturn(identidad("sub-1", "a@x.com", true));
        when(usuarios.buscarPorGoogleSubject("sub-1")).thenReturn(Optional.empty());
        when(usuarios.buscarPorEmail("a@x.com")).thenReturn(Optional.of(cuentaLocal));

        assertThatThrownBy(() -> useCase.execute(solicitud))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo("CUENTA_EXISTENTE_REQUIERE_VINCULACION");

        verify(usuarios, never()).guardar(any());
        verifyNoInteractions(tokenGenerator);
    }
}
