package com.andinaseguros.core.ports.in.tarifa;

import com.andinaseguros.core.application.dto.Responses.TablaDetalleResponse;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import java.util.UUID;

public interface ObtenerTablaTarifariaUseCase {
    TablaDetalleResponse execute(UUID tablaTarifariaId);
}
