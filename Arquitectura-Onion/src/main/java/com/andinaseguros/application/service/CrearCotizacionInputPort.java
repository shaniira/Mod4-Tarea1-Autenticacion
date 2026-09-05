package com.andinaseguros.application.service;

import com.andinaseguros.application.dto.CrearCotizacionDto;
import com.andinaseguros.application.dto.Responses.CotizacionResponse;

public interface CrearCotizacionInputPort {
    CotizacionResponse execute(CrearCotizacionDto solicitud);
}
