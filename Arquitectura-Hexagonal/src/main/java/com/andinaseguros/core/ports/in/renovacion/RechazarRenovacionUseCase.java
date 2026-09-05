package com.andinaseguros.core.ports.in.renovacion;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import java.time.LocalDateTime;
import java.util.UUID;

public interface RechazarRenovacionUseCase {
    RenovacionResponse execute(UUID renovacionId);
}
