package com.andinaseguros.core.application.mapper;

import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.domain.model.Poliza;

public final class PolizaResponseMapper {
    private PolizaResponseMapper() {}

    public static PolizaResponse toResponse(Poliza poliza) {
        return new PolizaResponse(
                poliza.getId(),
                poliza.getNumero(),
                poliza.getCotizacionId(),
                poliza.getClienteId(),
                poliza.getVehiculoId(),
                poliza.getPrima().valor(),
                poliza.getPrima().moneda(),
                poliza.getVigencia().inicio(),
                poliza.getVigencia().fin(),
                poliza.getEstado(),
                poliza.getRenovacionOrigenId());
    }
}
