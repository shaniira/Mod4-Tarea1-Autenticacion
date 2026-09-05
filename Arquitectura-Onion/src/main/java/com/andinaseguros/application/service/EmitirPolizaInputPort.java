package com.andinaseguros.application.service;

import com.andinaseguros.application.dto.EmitirPolizaDto;
import com.andinaseguros.application.dto.Responses.PolizaResponse;

public interface EmitirPolizaInputPort {
    PolizaResponse execute(EmitirPolizaDto solicitud);
}
