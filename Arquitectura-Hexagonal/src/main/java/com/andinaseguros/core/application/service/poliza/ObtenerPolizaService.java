package com.andinaseguros.core.application.service.poliza;

import com.andinaseguros.core.ports.in.poliza.ObtenerPolizaUseCase;

import static com.andinaseguros.core.application.mapper.PolizaResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import java.util.UUID;

public class ObtenerPolizaService implements ObtenerPolizaUseCase {

    private final PolizaRepositoryPort polizaRepository;

    public ObtenerPolizaService(PolizaRepositoryPort polizaRepository) {
        this.polizaRepository = polizaRepository;
    }

    public PolizaResponse execute(UUID polizaId) {
        Poliza poliza =
                polizaRepository
                        .buscarPorId(polizaId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Póliza"));

        return toResponse(poliza);
    }
}
