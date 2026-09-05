package com.andinaseguros.adapters.inbound.rest.mapper;

import com.andinaseguros.adapters.inbound.rest.response.VehicleInformationRestResponse;
import com.andinaseguros.core.application.dto.VehicleInformation;

public final class VehicleInformationRestMapper {
    private VehicleInformationRestMapper() {}

    public static VehicleInformationRestResponse toRestResponse(VehicleInformation information) {
        return VehicleInformationRestResponse.from(information);
    }
}