package com.andinaseguros.application.service;

import com.andinaseguros.application.exception.VehicleProviderException;
import com.andinaseguros.application.service.ConsultarInformacionVehiculoUseCase;
import com.andinaseguros.domain.valueobject.Placa;
import com.andinaseguros.application.gateway.vehicle.VehicleInformation;
import com.andinaseguros.application.gateway.vehicle.VehicleInformationPort;
import java.util.Locale;

public class ConsultarInformacionVehiculoService implements ConsultarInformacionVehiculoUseCase {
    private final VehicleInformationPort vehicleInformationPort;

    public ConsultarInformacionVehiculoService(VehicleInformationPort vehicleInformationPort) {
        this.vehicleInformationPort = vehicleInformationPort;
    }

    @Override
    public VehicleInformation consultarPorPlaca(String placa) {
        String valorLimpio =
                placa == null
                        ? null
                        : placa.trim().replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
        String normalizada = new Placa(valorLimpio).valor();
        try {
            return vehicleInformationPort
                    .consultarPorPlaca(normalizada)
                    .orElseGet(() -> sinDatos(normalizada));
        } catch (VehicleProviderException exception) {
            return sinDatos(normalizada);
        }
    }

    private VehicleInformation sinDatos(String placa) {
        return new VehicleInformation(placa, null, null, null, null, null, null, "SIN_DATOS");
    }
}
