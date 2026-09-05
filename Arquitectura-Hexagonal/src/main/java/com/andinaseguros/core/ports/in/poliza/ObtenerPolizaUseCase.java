package com.andinaseguros.core.ports.in.poliza;

import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.Poliza;
import java.util.UUID;

public interface ObtenerPolizaUseCase {
    PolizaResponse execute(UUID polizaId);
}
