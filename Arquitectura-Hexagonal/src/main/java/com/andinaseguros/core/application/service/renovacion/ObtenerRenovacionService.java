package com.andinaseguros.core.application.service.renovacion;

import com.andinaseguros.core.ports.in.renovacion.ObtenerRenovacionUseCase;

import static com.andinaseguros.core.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import java.util.UUID;

public class ObtenerRenovacionService implements ObtenerRenovacionUseCase {

    private final RenovacionRepositoryPort renovacionRepository;

    public ObtenerRenovacionService(RenovacionRepositoryPort renovacionRepository) {
        this.renovacionRepository = renovacionRepository;
    }

    public RenovacionResponse execute(UUID renovacionId) {
        PropuestaRenovacion propuesta =
                renovacionRepository
                        .buscarPorId(renovacionId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Renovación"));

        return toResponse(propuesta);
    }
}
