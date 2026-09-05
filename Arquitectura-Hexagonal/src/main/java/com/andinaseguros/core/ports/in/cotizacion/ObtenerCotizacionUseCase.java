package com.andinaseguros.core.ports.in.cotizacion;

import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.Cotizacion;
import java.util.UUID;

public interface ObtenerCotizacionUseCase {
    CotizacionResponse execute(UUID cotizacionId);
}
