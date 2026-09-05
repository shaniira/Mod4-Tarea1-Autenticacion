package com.andinaseguros.application.service.cotizacion;

import static com.andinaseguros.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.domain.enums.EstadoCotizacion;
import com.andinaseguros.domain.repository.CotizacionRepository;
import com.andinaseguros.domain.repository.PolizaRepository;
import java.util.List;

public class ListarCotizacionesPendientesEmisionUseCase {
    private final CotizacionRepository cotizaciones;
    private final PolizaRepository polizas;

    public ListarCotizacionesPendientesEmisionUseCase(
            CotizacionRepository cotizaciones, PolizaRepository polizas) {
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
