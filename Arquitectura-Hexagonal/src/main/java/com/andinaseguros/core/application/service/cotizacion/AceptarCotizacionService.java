package com.andinaseguros.core.application.service.cotizacion;

import com.andinaseguros.core.ports.in.cotizacion.AceptarCotizacionUseCase;

import static com.andinaseguros.core.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import java.util.UUID;

public class AceptarCotizacionService implements AceptarCotizacionUseCase {

    private final CotizacionRepositoryPort cotizacionRepository;

    public AceptarCotizacionService(CotizacionRepositoryPort cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    public CotizacionResponse execute(UUID cotizacionId) {
        Cotizacion cotizacion =
                cotizacionRepository
                        .buscarPorId(cotizacionId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Cotización"));

        cotizacion.aceptar();

        Cotizacion cotizacionGuardada = cotizacionRepository.guardar(cotizacion);

        return toResponse(cotizacionGuardada, null);
    }
}
