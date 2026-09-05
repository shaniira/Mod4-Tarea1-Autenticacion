package com.andinaseguros.core.ports.in.vehiculo;

import com.andinaseguros.core.application.dto.VehicleInformation;

public interface ConsultarInformacionVehiculoUseCase {
    VehicleInformation consultarPorPlaca(String placa);
}
