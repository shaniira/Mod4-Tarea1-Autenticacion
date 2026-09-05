package com.andinaseguros.application.service.cotizacion;

import static com.andinaseguros.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.model.Cotizacion;
import com.andinaseguros.domain.repository.CotizacionRepository;
import java.util.UUID;

public class ObtenerCotizacionUseCase {

    private final CotizacionRepository cotizacionRepository;

    public ObtenerCotizacionUseCase(CotizacionRepository cotizacionRepository) {
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
