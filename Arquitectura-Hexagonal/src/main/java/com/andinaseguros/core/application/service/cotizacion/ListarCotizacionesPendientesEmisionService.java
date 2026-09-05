package com.andinaseguros.core.application.service.cotizacion;

import com.andinaseguros.core.ports.in.cotizacion.ListarCotizacionesPendientesEmisionUseCase;

import static com.andinaseguros.core.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import java.util.List;

public class ListarCotizacionesPendientesEmisionService implements ListarCotizacionesPendientesEmisionUseCase {
    private final CotizacionRepositoryPort cotizaciones;
    private final PolizaRepositoryPort polizas;

    public ListarCotizacionesPendientesEmisionService(
            CotizacionRepositoryPort cotizaciones, PolizaRepositoryPort polizas) {
        this.cotizaciones = cotizaciones;
        this.polizas = polizas;
    }

    public List<CotizacionResponse> execute() {
        return cotizaciones.listarPorEstado(EstadoCotizacion.ACEPTADA).stream()
                .filter(cotizacion -> polizas.buscarPorCotizacionId(cotizacion.getId()).isEmpty())
                .map(cotizacion -> toResponse(cotizacion, null))
                .toList();
    }
}
