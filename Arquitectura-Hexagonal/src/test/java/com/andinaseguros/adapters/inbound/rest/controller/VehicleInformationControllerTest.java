package com.andinaseguros.adapters.inbound.rest.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.andinaseguros.core.application.dto.VehicleInformation;
import com.andinaseguros.core.ports.in.vehiculo.ConsultarInformacionVehiculoUseCase;
import org.junit.jupiter.api.Test;

class VehicleInformationControllerTest {
    @Test
    void retornaHttp200SinAccederAlProveedor() {
        var useCase = mock(ConsultarInformacionVehiculoUseCase.class);
        when(useCase.consultarPorPlaca("B6U170"))
                .thenReturn(
                        new VehicleInformation(
                                "B6U170", "RENAULT", "LOGAN", 2011, null, null, null, "FAKE"));
        var response = new VehicleInformationController(useCase).consultar("B6U170");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().fuente()).isEqualTo("FAKE");
    }
}
