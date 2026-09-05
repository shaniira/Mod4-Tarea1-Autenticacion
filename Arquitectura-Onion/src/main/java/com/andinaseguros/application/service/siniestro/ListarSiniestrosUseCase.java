package com.andinaseguros.application.service.siniestro;

import static com.andinaseguros.application.mapper.SiniestroResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.domain.repository.SiniestroRepository;
import java.util.List;
import java.util.UUID;

public class ListarSiniestrosUseCase {

    private final SiniestroRepository siniestroRepository;

    public ListarSiniestrosUseCase(SiniestroRepository siniestroRepository) {
        this.siniestroRepository = siniestroRepository;
    }

    public List<SiniestroResponse> execute(UUID polizaId) {
        return siniestroRepository.listarPorPoliza(polizaId).stream()
                .map(siniestro -> toResponse(siniestro))
                .toList();
    }
}
