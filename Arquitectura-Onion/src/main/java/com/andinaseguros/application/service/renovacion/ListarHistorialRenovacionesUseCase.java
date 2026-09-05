package com.andinaseguros.application.service.renovacion;

import static com.andinaseguros.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.repository.PolizaRepository;
import com.andinaseguros.domain.repository.RenovacionRepository;
import java.util.List;
import java.util.UUID;

public class ListarHistorialRenovacionesUseCase {

    private final PolizaRepository polizaRepository;
    private final RenovacionRepository renovacionRepository;

    public ListarHistorialRenovacionesUseCase(
            PolizaRepository polizaRepository, RenovacionRepository renovacionRepository) {
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
