package com.andinaseguros.core.application.mapper;

import com.andinaseguros.core.application.dto.Responses.VehiculoResponse;
import com.andinaseguros.core.domain.model.Vehiculo;

public final class VehiculoResponseMapper {
    private VehiculoResponseMapper() {}

    public static VehiculoResponse toResponse(Vehiculo vehiculo) {
        return new VehiculoResponse(
                vehiculo.getId(),
                vehiculo.getClienteId(),
                vehiculo.getPlaca().valor(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnioFabricacion(),
                vehiculo.getTipo(),
                vehiculo.getUso(),
                vehiculo.getZonaCirculacion());
    }
}
