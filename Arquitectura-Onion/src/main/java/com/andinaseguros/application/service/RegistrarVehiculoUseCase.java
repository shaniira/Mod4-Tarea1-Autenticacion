package com.andinaseguros.application.service;

import com.andinaseguros.application.dto.CrearVehiculoDto;
import com.andinaseguros.application.dto.Responses.VehiculoResponse;

public interface RegistrarVehiculoUseCase {
    VehiculoResponse execute(CrearVehiculoDto solicitud);
}
