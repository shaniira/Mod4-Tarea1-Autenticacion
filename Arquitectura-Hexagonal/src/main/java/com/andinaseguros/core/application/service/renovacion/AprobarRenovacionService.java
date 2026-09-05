package com.andinaseguros.core.application.service.renovacion;

import com.andinaseguros.core.ports.in.renovacion.AprobarRenovacionUseCase;

import static com.andinaseguros.core.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;

public class AprobarRenovacionService implements AprobarRenovacionUseCase {

    private final RenovacionRepositoryPort renovacionRepository;

    public AprobarRenovacionService(RenovacionRepositoryPort renovacionRepository) {
        this.renovacionRepository = renovacionRepository;
    }

    public RenovacionResponse execute(UUID renovacionId) {
        PropuestaRenovacion propuesta =
                renovacionRepository
                        .buscarPorId(renovacionId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Renovación"));

        propuesta.aprobar(LocalDateTime.now());

        PropuestaRenovacion propuestaGuardada = renovacionRepository.guardar(propuesta);

        return toResponse(propuestaGuardada);
    }
}
