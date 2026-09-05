package com.andinaseguros.application.mapper;

import com.andinaseguros.application.dto.Responses.PolizaResponse;
import com.andinaseguros.domain.model.Poliza;

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
