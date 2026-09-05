package com.andinaseguros.core.application.service.renovacion;

import com.andinaseguros.core.ports.in.renovacion.ListarRenovacionesUseCase;

import static com.andinaseguros.core.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import java.util.List;

public class ListarRenovacionesService implements ListarRenovacionesUseCase {

    private final RenovacionRepositoryPort renovacionRepository;

    public ListarRenovacionesService(RenovacionRepositoryPort renovacionRepository) {
        this.renovacionRepository = renovacionRepository;
    }

    public List<RenovacionResponse> execute() {
        return renovacionRepository.listar().stream()
                .map(propuesta -> toResponse(propuesta))
                .toList();
    }
}
