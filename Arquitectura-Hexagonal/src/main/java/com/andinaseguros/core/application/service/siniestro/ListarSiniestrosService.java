package com.andinaseguros.core.application.service.siniestro;

import com.andinaseguros.core.ports.in.siniestro.ListarSiniestrosUseCase;

import static com.andinaseguros.core.application.mapper.SiniestroResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import java.util.List;
import java.util.UUID;

public class ListarSiniestrosService implements ListarSiniestrosUseCase {

    private final SiniestroRepositoryPort siniestroRepository;

    public ListarSiniestrosService(SiniestroRepositoryPort siniestroRepository) {
        this.siniestroRepository = siniestroRepository;
    }

    public List<SiniestroResponse> execute(UUID polizaId) {
        return siniestroRepository.listarPorPoliza(polizaId).stream()
                .map(siniestro -> toResponse(siniestro))
                .toList();
    }
}
