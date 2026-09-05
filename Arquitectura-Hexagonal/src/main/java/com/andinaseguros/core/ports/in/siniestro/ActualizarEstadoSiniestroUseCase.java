package com.andinaseguros.core.ports.in.siniestro;

import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoSiniestro;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Siniestro;
import java.util.UUID;

public interface ActualizarEstadoSiniestroUseCase {
    SiniestroResponse execute(UUID polizaId, UUID siniestroId, EstadoSiniestro nuevoEstado);
}
