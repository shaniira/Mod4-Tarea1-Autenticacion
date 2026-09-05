package com.andinaseguros.application.service.tarifa;

import static com.andinaseguros.application.mapper.TablaTarifariaResponseMapper.toDetailResponse;

import com.andinaseguros.application.dto.Responses.TablaDetalleResponse;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.model.TablaTarifaria;
import com.andinaseguros.domain.repository.TablaTarifariaRepository;
import java.util.UUID;

public class ObtenerTablaTarifariaUseCase {

    private final TablaTarifariaRepository tablaTarifariaRepository;

    public ObtenerTablaTarifariaUseCase(TablaTarifariaRepository tablaTarifariaRepository) {
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
