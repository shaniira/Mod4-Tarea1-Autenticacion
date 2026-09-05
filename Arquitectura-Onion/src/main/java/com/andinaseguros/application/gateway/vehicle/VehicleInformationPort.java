package com.andinaseguros.application.gateway.vehicle;

import java.util.Optional;

public interface VehicleInformationPort {
    Optional<VehicleInformation> consultarPorPlaca(String placa);
}
