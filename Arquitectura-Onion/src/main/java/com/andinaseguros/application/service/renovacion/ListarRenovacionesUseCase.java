package com.andinaseguros.application.service.renovacion;

import static com.andinaseguros.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.domain.repository.RenovacionRepository;
import java.util.List;

public class ListarRenovacionesUseCase {

    private final RenovacionRepository renovacionRepository;

    public ListarRenovacionesUseCase(RenovacionRepository renovacionRepository) {
        this.renovacionRepository = renovacionRepository;
    }

    public List<RenovacionResponse> execute() {
        return renovacionRepository.listar().stream()
                .map(propuesta -> toResponse(propuesta))
                .toList();
    }
}
