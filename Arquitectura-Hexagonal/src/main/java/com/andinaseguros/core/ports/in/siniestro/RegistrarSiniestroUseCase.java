package com.andinaseguros.core.ports.in.siniestro;

import com.andinaseguros.core.application.dto.RegistrarSiniestroCommand;
import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.domain.model.Siniestro;
import com.andinaseguros.core.domain.valueobject.Dinero;
import java.util.UUID;

public interface RegistrarSiniestroUseCase {
    SiniestroResponse execute(RegistrarSiniestroCommand solicitud);
}
