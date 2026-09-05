package com.andinaseguros.core.ports.in.cotizacion;

import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import java.util.List;

public interface ListarCotizacionesPendientesEmisionUseCase {
    List<CotizacionResponse> execute();
}
