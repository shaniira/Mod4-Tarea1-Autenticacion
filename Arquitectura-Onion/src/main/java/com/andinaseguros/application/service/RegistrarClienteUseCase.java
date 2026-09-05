package com.andinaseguros.application.service;

import com.andinaseguros.application.dto.CrearClienteDto;
import com.andinaseguros.application.dto.Responses.ClienteResponse;

public interface RegistrarClienteUseCase {
    ClienteResponse execute(CrearClienteDto solicitud);
}
