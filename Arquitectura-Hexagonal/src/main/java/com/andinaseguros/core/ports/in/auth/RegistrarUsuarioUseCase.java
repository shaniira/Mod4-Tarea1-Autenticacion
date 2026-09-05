package com.andinaseguros.core.ports.in.auth;

import com.andinaseguros.core.application.dto.CrearUsuarioCommand;
import com.andinaseguros.core.ports.out.id.IdGeneratorPort;
import com.andinaseguros.core.ports.out.persistence.UsuarioRepositoryPort;
import com.andinaseguros.core.ports.out.security.PasswordEncoderPort;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Usuario;

public interface RegistrarUsuarioUseCase {
    void execute(CrearUsuarioCommand solicitud);
}
