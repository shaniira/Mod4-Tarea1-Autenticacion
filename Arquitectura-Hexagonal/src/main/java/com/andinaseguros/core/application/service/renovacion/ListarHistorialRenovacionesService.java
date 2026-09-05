package com.andinaseguros.core.application.service.renovacion;

import com.andinaseguros.core.ports.in.renovacion.ListarHistorialRenovacionesUseCase;

import static com.andinaseguros.core.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import java.util.List;
import java.util.UUID;

public class ListarHistorialRenovacionesService implements ListarHistorialRenovacionesUseCase {

    private final PolizaRepositoryPort polizaRepository;
    private final RenovacionRepositoryPort renovacionRepository;

    public ListarHistorialRenovacionesService(
            PolizaRepositoryPort polizaRepository, RenovacionRepositoryPort renovacionRepository) {
        this.polizaRepository = polizaRepository;
        this.renovacionRepository = renovacionRepository;
    }

    public List<RenovacionResponse> execute(UUID polizaId) {
        polizaRepository
                .buscarPorId(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Póliza"));

        return renovacionRepository.listarPorPoliza(polizaId).stream()
                .map(propuesta -> toResponse(propuesta))
                .toList();
    }
}
