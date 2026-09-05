package com.andinaseguros.core.ports.out.external.vehicle;

import com.andinaseguros.core.application.dto.VehicleInformation;
import java.util.Optional;

public interface VehicleInformationPort {
    Optional<VehicleInformation> consultarPorPlaca(String placa);
}
