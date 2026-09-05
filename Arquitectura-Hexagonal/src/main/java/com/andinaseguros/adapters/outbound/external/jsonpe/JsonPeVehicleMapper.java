package com.andinaseguros.adapters.outbound.external.jsonpe;

import com.andinaseguros.core.application.dto.VehicleInformation;

public class JsonPeVehicleMapper {
    VehicleInformation toDomain(JsonPePlateResponse response) {
        JsonPeVehicleData d = response.data();
        return new VehicleInformation(
                d.placa(),
                d.marca(),
                d.modelo(),
                null,
                null,
                (d.marca() + " " + d.modelo()).trim(),
                d.vin(),
                "JSON_PE");
    }
}
