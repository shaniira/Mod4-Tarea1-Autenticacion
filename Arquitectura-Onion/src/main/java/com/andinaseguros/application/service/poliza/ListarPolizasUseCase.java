package com.andinaseguros.application.service.poliza;

import static com.andinaseguros.application.mapper.PolizaResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.PolizaResponse;
import com.andinaseguros.domain.enums.EstadoPoliza;
import com.andinaseguros.domain.repository.PolizaRepository;
import java.util.List;

public class ListarPolizasUseCase {

    private final PolizaRepository polizaRepository;

    public ListarPolizasUseCase(PolizaRepository polizaRepository) {
        this.polizaRepository = polizaRepository;
    }

    public List<PolizaResponse> execute() {
        return execute(null);
    }

    public List<PolizaResponse> execute(EstadoPoliza estado) {
        return polizaRepository.listar().stream()
                .filter(poliza -> estado == null || poliza.getEstado() == estado)
                .map(poliza -> toResponse(poliza))
                .toList();
    }
}
