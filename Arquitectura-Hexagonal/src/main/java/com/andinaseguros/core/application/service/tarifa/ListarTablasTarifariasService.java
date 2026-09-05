package com.andinaseguros.core.application.service.tarifa;

import com.andinaseguros.core.ports.in.tarifa.ListarTablasTarifariasUseCase;

import static com.andinaseguros.core.application.mapper.TablaTarifariaResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.TablaResponse;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import java.util.List;

public class ListarTablasTarifariasService implements ListarTablasTarifariasUseCase {

    private final TablaTarifariaRepositoryPort tablaTarifariaRepository;

    public ListarTablasTarifariasService(TablaTarifariaRepositoryPort tablaTarifariaRepository) {
        this.tablaTarifariaRepository = tablaTarifariaRepository;
    }

    public List<TablaResponse> execute() {
        return tablaTarifariaRepository.listar().stream().map(tabla -> toResponse(tabla)).toList();
    }
}
