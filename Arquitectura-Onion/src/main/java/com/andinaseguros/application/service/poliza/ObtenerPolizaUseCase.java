package com.andinaseguros.application.service.poliza;

import static com.andinaseguros.application.mapper.PolizaResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.PolizaResponse;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.model.Poliza;
import com.andinaseguros.domain.repository.PolizaRepository;
import java.util.UUID;

public class ObtenerPolizaUseCase {

    private final PolizaRepository polizaRepository;

    public ObtenerPolizaUseCase(PolizaRepository polizaRepository) {
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
