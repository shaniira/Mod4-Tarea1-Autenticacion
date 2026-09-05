package com.andinaseguros.infrastructure.event;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.andinaseguros.domain.event.PolizaEmitidaEvent;
import com.andinaseguros.application.gateway.contact.*;
import com.andinaseguros.application.gateway.notification.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PolizaEmitidaNotificationHandlerTest {
    @Test
    void resuelveContactoYEnviaMensajeNeutral() {
        NotificationPort notificationPort = mock(NotificationPort.class);
        ClienteContactPort contactPort = mock(ClienteContactPort.class);
        UUID clienteId = UUID.randomUUID();
        when(contactPort.buscarPorClienteId(clienteId))
                .thenReturn(
                        Optional.of(
                                new ClienteContactData(
                                        clienteId,
                                        "Ana Pérez",
                                        "cliente@example.com",
                                        "+51999999999")));
        when(notificationPort.enviar(any()))
                .thenReturn(new NotificationResult(true, "WHATSAPP", "exito"));

        new PolizaEmitidaNotificationHandler(notificationPort, contactPort)
                .manejar(evento(clienteId));

        ArgumentCaptor<NotificationMessage> captor =
                ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationPort).enviar(captor.capture());
        verify(contactPort).buscarPorClienteId(clienteId);
        org.assertj.core.api.Assertions.assertThat(captor.getValue().destinatario())
                .isEqualTo("+51999999999");
        org.assertj.core.api.Assertions.assertThat(captor.getValue().parametros())
                .containsEntry("nombreCliente", "Ana Pérez")
                .containsEntry("numeroPoliza", "POL-001");
    }

    @Test
    void falloDeNotificacionNoPropagaExcepcion() {
        NotificationPort notificationPort = mock(NotificationPort.class);
        ClienteContactPort contactPort = mock(ClienteContactPort.class);
        UUID clienteId = UUID.randomUUID();
        when(contactPort.buscarPorClienteId(clienteId))
                .thenReturn(
                        Optional.of(
                                new ClienteContactData(
                                        clienteId, "Ana", "cliente@example.com", "+51999999999")));
        when(notificationPort.enviar(any()))
                .thenThrow(new IllegalStateException("Proveedor caído"));

        assertThatCode(
                        () ->
                                new PolizaEmitidaNotificationHandler(notificationPort, contactPort)
                                        .manejar(evento(clienteId)))
                .doesNotThrowAnyException();
    }

    private PolizaEmitidaEvent evento(UUID clienteId) {
        return new PolizaEmitidaEvent(
                UUID.randomUUID(),
                Instant.parse("2026-07-31T12:00:00Z"),
                UUID.randomUUID(),
                UUID.randomUUID(),
                clienteId,
                "POL-001");
    }
}
