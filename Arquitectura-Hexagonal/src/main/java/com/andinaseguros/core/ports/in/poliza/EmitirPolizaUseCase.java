package com.andinaseguros.core.ports.in.poliza;

import com.andinaseguros.core.application.dto.EmitirPolizaCommand;
import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.ports.out.event.DomainEventPublisherPort;
import com.andinaseguros.core.ports.out.id.IdGeneratorPort;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.clock.ClockPort;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.event.PolizaEmitidaEvent;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.domain.valueobject.PeriodoVigencia;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;

public interface EmitirPolizaUseCase {
    PolizaResponse execute(EmitirPolizaCommand solicitud);
}
