package com.andinaseguros.core.ports.in.poliza;

import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import java.util.List;

public interface ListarPolizasUseCase {
    List<PolizaResponse> execute();
    List<PolizaResponse> execute(EstadoPoliza estado);
}
