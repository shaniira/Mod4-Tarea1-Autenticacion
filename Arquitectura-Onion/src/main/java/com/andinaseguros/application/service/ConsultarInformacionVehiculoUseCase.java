package com.andinaseguros.application.service;

import com.andinaseguros.application.gateway.vehicle.VehicleInformation;

public interface ConsultarInformacionVehiculoUseCase {
    VehicleInformation consultarPorPlaca(String placa);
}
