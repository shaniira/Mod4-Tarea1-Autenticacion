package com.andinaseguros.application.service.cotizacion;

import static com.andinaseguros.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.domain.enums.EstadoCotizacion;
import com.andinaseguros.domain.repository.CotizacionRepository;
import java.util.List;

public class ListarCotizacionesUseCase {
    private final CotizacionRepository repository;

    public ListarCotizacionesUseCase(CotizacionRepository repository) {
        this.repository = repository;
    }

    public List<CotizacionResponse> execute(EstadoCotizacion estado) {
        var cotizaciones =
                estado == null ? repository.listar() : repository.listarPorEstado(estado);
        return cotizaciones.stream().map(cotizacion -> toResponse(cotizacion, null)).toList();
    }
}
