package com.andinaseguros.core.ports.in.renovacion;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import java.util.List;

public interface ListarRenovacionesUseCase {
    List<RenovacionResponse> execute();
}
