package com.andinaseguros.core.ports.in.renovacion;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import java.util.List;
import java.util.UUID;

public interface ListarHistorialRenovacionesUseCase {
    List<RenovacionResponse> execute(UUID polizaId);
}
