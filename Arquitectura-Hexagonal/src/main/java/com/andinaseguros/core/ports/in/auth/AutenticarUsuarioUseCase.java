package com.andinaseguros.core.ports.in.auth;

import com.andinaseguros.core.application.dto.LoginCommand;
import com.andinaseguros.core.application.dto.Responses.TokenResponse;
import com.andinaseguros.core.ports.out.persistence.UsuarioRepositoryPort;
import com.andinaseguros.core.ports.out.security.*;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;

public interface AutenticarUsuarioUseCase {
    TokenResponse execute(LoginCommand solicitud);
}
