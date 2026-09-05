package com.andinaseguros.core.ports.in.tarifa;

import com.andinaseguros.core.application.dto.Responses.TablaResponse;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import java.util.List;

public interface ListarTablasTarifariasUseCase {
    List<TablaResponse> execute();
}
