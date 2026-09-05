package com.andinaseguros.core.ports.in.renovacion;

import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.enums.EstadoRenovacion;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import com.andinaseguros.core.domain.valueobject.PeriodoVigencia;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

public interface GenerarPolizaRenovadaUseCase {
    PolizaResponse execute(UUID renovacionId);
}
