package com.andinaseguros.core.application.service.tarifa;

import com.andinaseguros.core.ports.in.tarifa.ObtenerTablaTarifariaUseCase;

import static com.andinaseguros.core.application.mapper.TablaTarifariaResponseMapper.toDetailResponse;

import com.andinaseguros.core.application.dto.Responses.TablaDetalleResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import java.util.UUID;

public class ObtenerTablaTarifariaService implements ObtenerTablaTarifariaUseCase {

    private final TablaTarifariaRepositoryPort tablaTarifariaRepository;

    public ObtenerTablaTarifariaService(TablaTarifariaRepositoryPort tablaTarifariaRepository) {
        this.tablaTarifariaRepository = tablaTarifariaRepository;
    }

    public TablaDetalleResponse execute(UUID tablaTarifariaId) {
        TablaTarifaria tablaTarifaria =
                tablaTarifariaRepository
                        .buscarPorId(tablaTarifariaId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Tabla tarifaria"));

        return toDetailResponse(tablaTarifaria);
    }
}
