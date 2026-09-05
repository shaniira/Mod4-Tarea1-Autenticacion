package com.andinaseguros.core.application.mapper;

import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.core.domain.model.Siniestro;

public final class SiniestroResponseMapper {
    private SiniestroResponseMapper() {}

    public static SiniestroResponse toResponse(Siniestro siniestro) {
        return new SiniestroResponse(
                siniestro.id(),
                siniestro.polizaId(),
                siniestro.fecha(),
                siniestro.tipo(),
                siniestro.montoEstimado().valor(),
                siniestro.responsabilidadAsegurado(),
                siniestro.gravedad(),
                siniestro.estado());
    }
}
