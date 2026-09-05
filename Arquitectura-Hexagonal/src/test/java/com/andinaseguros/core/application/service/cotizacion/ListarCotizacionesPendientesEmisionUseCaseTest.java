package com.andinaseguros.core.application.service.cotizacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.domain.valueobject.Dinero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ListarCotizacionesPendientesEmisionUseCaseTest {
    @Test
    void excluyeCotizacionesQueYaTienenPoliza() {
        var cotizaciones = mock(CotizacionRepositoryPort.class);
        var polizas = mock(PolizaRepositoryPort.class);
        var pendiente = cotizacion();
        var emitida = cotizacion();
        when(cotizaciones.listarPorEstado(EstadoCotizacion.ACEPTADA))
                .thenReturn(List.of(pendiente, emitida));
        when(polizas.buscarPorCotizacionId(pendiente.getId())).thenReturn(Optional.empty());
        when(polizas.buscarPorCotizacionId(emitida.getId()))
                .thenReturn(Optional.of(mock(Poliza.class)));

        var resultado =
                new ListarCotizacionesPendientesEmisionService(cotizaciones, polizas).execute();

        assertThat(resultado).extracting(item -> item.id()).containsExactly(pendiente.getId());
    }

    private Cotizacion cotizacion() {
        return new Cotizacion(
                UUID.randomUUID(),
                "COT-TEST",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Dinero.soles(new BigDecimal("1200")),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                EstadoCotizacion.ACEPTADA);
    }
}
