package com.andinaseguros.core.application.service.poliza;

import com.andinaseguros.core.ports.in.poliza.ListarPolizasUseCase;

import static com.andinaseguros.core.application.mapper.PolizaResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import java.util.List;

public class ListarPolizasService implements ListarPolizasUseCase {

    private final PolizaRepositoryPort polizaRepository;

    public ListarPolizasService(PolizaRepositoryPort polizaRepository) {
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
