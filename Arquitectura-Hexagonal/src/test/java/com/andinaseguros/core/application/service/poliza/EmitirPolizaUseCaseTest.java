package com.andinaseguros.core.application.service.poliza;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andinaseguros.core.application.dto.EmitirPolizaCommand;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.*;
import com.andinaseguros.core.ports.out.persistence.*;
import com.andinaseguros.core.domain.valueobject.Dinero;
import com.andinaseguros.core.ports.out.event.DomainEventPublisherPort;
import com.andinaseguros.core.ports.out.id.IdGeneratorPort;
import com.andinaseguros.core.ports.out.clock.ClockPort;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class EmitirPolizaUseCaseTest {
    private final CotizacionRepositoryPort cotizaciones = mock(CotizacionRepositoryPort.class);
    private final PolizaRepositoryPort polizas = mock(PolizaRepositoryPort.class);
    private final DomainEventPublisherPort eventos = mock(DomainEventPublisherPort.class);
    private final ClockPort clock = mock(ClockPort.class);
    private final IdGeneratorPort ids = mock(IdGeneratorPort.class);
    private final UUID cotizacionId = UUID.randomUUID();
    private final UUID clienteId = UUID.randomUUID();
    private final Cotizacion cotizacion =
            new Cotizacion(
                    cotizacionId,
                    "COT-1",
                    clienteId,
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new Dinero(new BigDecimal("1000"), "PEN"),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1),
                    EstadoCotizacion.ACEPTADA);

    @Test
    void emiteYPublicaEvento() {
        when(cotizaciones.buscarPorId(cotizacionId)).thenReturn(Optional.of(cotizacion));
        when(polizas.buscarPorCotizacionId(cotizacionId)).thenReturn(Optional.empty());
        when(polizas.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(clock.now()).thenReturn(Instant.parse("2026-07-27T12:00:00Z"));
        when(ids.generar()).thenReturn(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var useCase = new EmitirPolizaService(cotizaciones, polizas, eventos, clock, ids);

        var respuesta = useCase.execute(new EmitirPolizaCommand(cotizacionId, LocalDate.now()));

        assertThat(respuesta.cotizacionId()).isEqualTo(cotizacionId);
        verify(eventos).publicar(any());
    }

    @Test
    void impideDobleEmisionYNoPublicaEvento() {
        when(cotizaciones.buscarPorId(cotizacionId)).thenReturn(Optional.of(cotizacion));
        when(polizas.buscarPorCotizacionId(cotizacionId))
                .thenReturn(Optional.of(mock(Poliza.class)));
        var useCase = new EmitirPolizaService(cotizaciones, polizas, eventos, clock, ids);

        assertThatThrownBy(() -> useCase.execute(new EmitirPolizaCommand(cotizacionId, LocalDate.now())))
                .isInstanceOf(ReglaNegocioException.class);
        verifyNoInteractions(eventos);
    }
}
