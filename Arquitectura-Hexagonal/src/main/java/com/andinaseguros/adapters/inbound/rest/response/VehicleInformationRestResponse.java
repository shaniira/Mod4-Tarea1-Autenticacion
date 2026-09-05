package com.andinaseguros.adapters.inbound.rest.response;

import com.andinaseguros.core.application.dto.VehicleInformation;

public record VehicleInformationRestResponse(
        String placa,
        String marca,
        String modelo,
        Integer anio,
        String tipoUso,
        String descripcion,
        String vin,
        String fuente,
        String mensaje) {
    public static VehicleInformationRestResponse from(VehicleInformation information) {
        String mensaje =
                "SIN_DATOS".equals(information.fuente())
                        ? "No se encontraron datos externos. Complete el registro manualmente."
                        : "Datos externos encontrados. Verifique antes de guardar.";
        return new VehicleInformationRestResponse(
                information.placa(),
                information.marca(),
                information.modelo(),
                information.anio(),
                information.tipoUso(),
                information.descripcion(),
                information.vin(),
                information.fuente(),
                mensaje);
    }
}
