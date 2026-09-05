package com.andinaseguros.core.ports.in.renovacion;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.enums.EstadoRenovacion;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import com.andinaseguros.core.domain.model.Siniestro;
import com.andinaseguros.core.domain.service.CalculadorPrimaRenovacion;
import com.andinaseguros.core.domain.service.EvaluadorRenovacion;
import com.andinaseguros.core.domain.service.PoliticaVariacionPrima;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EvaluarRenovacionUseCase {
    RenovacionResponse execute(UUID polizaId);
}
