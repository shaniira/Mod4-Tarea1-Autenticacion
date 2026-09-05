package com.andinaseguros.application.service.auth;

import com.andinaseguros.application.dto.CrearUsuarioDto;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.model.Usuario;
import com.andinaseguros.domain.repository.UsuarioRepository;
import com.andinaseguros.application.gateway.id.IdGeneratorPort;
import com.andinaseguros.application.gateway.security.PasswordEncoderPort;

public class RegistrarUsuarioUseCase {
    private final UsuarioRepository usuarios;
    private final PasswordEncoderPort passwordEncoder;
    private final IdGeneratorPort ids;

    public RegistrarUsuarioUseCase(
            UsuarioRepository usuarios, PasswordEncoderPort passwordEncoder, IdGeneratorPort ids) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.ids = ids;
    }

    public void execute(CrearUsuarioDto solicitud) {
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
