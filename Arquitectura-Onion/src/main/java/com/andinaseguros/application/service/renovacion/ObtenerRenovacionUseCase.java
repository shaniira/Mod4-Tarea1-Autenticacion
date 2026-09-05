package com.andinaseguros.application.service.renovacion;

import static com.andinaseguros.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.model.PropuestaRenovacion;
import com.andinaseguros.domain.repository.RenovacionRepository;
import java.util.UUID;

public class ObtenerRenovacionUseCase {

    private final RenovacionRepository renovacionRepository;

    public ObtenerRenovacionUseCase(RenovacionRepository renovacionRepository) {
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
