package com.andinaseguros.application.service.tarifa;

import static com.andinaseguros.application.mapper.TablaTarifariaResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.TablaResponse;
import com.andinaseguros.domain.repository.TablaTarifariaRepository;
import java.util.List;

public class ListarTablasTarifariasUseCase {

    private final TablaTarifariaRepository tablaTarifariaRepository;

    public ListarTablasTarifariasUseCase(TablaTarifariaRepository tablaTarifariaRepository) {
        this.tablaTarifariaRepository = tablaTarifariaRepository;
    }

    public List<TablaResponse> execute() {
        return tablaTarifariaRepository.listar().stream().map(tabla -> toResponse(tabla)).toList();
    }
}
