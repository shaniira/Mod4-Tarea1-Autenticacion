package com.andinaseguros.core.application.service.cotizacion;

import com.andinaseguros.core.ports.in.cotizacion.ListarCotizacionesUseCase;

import static com.andinaseguros.core.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import java.util.List;

public class ListarCotizacionesService implements ListarCotizacionesUseCase {
    private final CotizacionRepositoryPort repository;

    public ListarCotizacionesService(CotizacionRepositoryPort repository) {
        this.repository = repository;
    }

    public List<CotizacionResponse> execute(EstadoCotizacion estado) {
        var cotizaciones =
                estado == null ? repository.listar() : repository.listarPorEstado(estado);
        return cotizaciones.stream().map(cotizacion -> toResponse(cotizacion, null)).toList();
    }
}
