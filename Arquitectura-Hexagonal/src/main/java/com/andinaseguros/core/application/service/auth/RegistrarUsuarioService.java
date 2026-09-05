package com.andinaseguros.core.application.service.auth;

import com.andinaseguros.core.ports.in.auth.RegistrarUsuarioUseCase;

import com.andinaseguros.core.application.dto.CrearUsuarioCommand;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Usuario;
import com.andinaseguros.core.ports.out.persistence.UsuarioRepositoryPort;
import com.andinaseguros.core.ports.out.id.IdGeneratorPort;
import com.andinaseguros.core.ports.out.security.PasswordEncoderPort;

public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {
    private final UsuarioRepositoryPort usuarios;
    private final PasswordEncoderPort passwordEncoder;
    private final IdGeneratorPort ids;

    public RegistrarUsuarioService(
            UsuarioRepositoryPort usuarios, PasswordEncoderPort passwordEncoder, IdGeneratorPort ids) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.ids = ids;
    }

    public void execute(CrearUsuarioCommand solicitud) {
        if (usuarios.buscarPorUsername(solicitud.username()).isPresent()) {
            throw new ReglaNegocioException("USUARIO_DUPLICADO", "El usuario ya existe");
        }
        usuarios.guardar(
                new Usuario(
                        ids.generar(),
                        solicitud.username(),
                        passwordEncoder.codificar(solicitud.password()),
                        solicitud.rol(),
                        true));
    }
}
