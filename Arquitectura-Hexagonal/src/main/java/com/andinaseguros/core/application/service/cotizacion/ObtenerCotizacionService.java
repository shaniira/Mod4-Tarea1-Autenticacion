package com.andinaseguros.core.application.service.cotizacion;

import com.andinaseguros.core.ports.in.cotizacion.ObtenerCotizacionUseCase;

import static com.andinaseguros.core.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import java.util.UUID;

public class ObtenerCotizacionService implements ObtenerCotizacionUseCase {

    private final CotizacionRepositoryPort cotizacionRepository;

    public ObtenerCotizacionService(CotizacionRepositoryPort cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    public CotizacionResponse execute(UUID cotizacionId) {
        Cotizacion cotizacion =
                cotizacionRepository
                        .buscarPorId(cotizacionId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Cotización"));

        return toResponse(cotizacion, null);
    }
}
