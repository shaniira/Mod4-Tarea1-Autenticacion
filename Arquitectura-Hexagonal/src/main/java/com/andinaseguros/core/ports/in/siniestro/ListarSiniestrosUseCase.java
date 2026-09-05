package com.andinaseguros.core.ports.in.siniestro;

import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import java.util.List;
import java.util.UUID;

public interface ListarSiniestrosUseCase {
    List<SiniestroResponse> execute(UUID polizaId);
}
