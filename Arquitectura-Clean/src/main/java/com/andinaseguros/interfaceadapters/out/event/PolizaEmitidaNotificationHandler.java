package com.andinaseguros.interfaceadapters.out.event;

import com.andinaseguros.entities.event.PolizaEmitidaEvent;
import com.andinaseguros.usecases.port.out.contact.ClienteContactData;
import com.andinaseguros.usecases.port.out.contact.ClienteContactPort;
import com.andinaseguros.usecases.port.out.notification.*;
import java.util.Map;
import org.slf4j.*;
import org.springframework.transaction.event.*;

public class PolizaEmitidaNotificationHandler {
    private static final Logger log =
            LoggerFactory.getLogger(PolizaEmitidaNotificationHandler.class);
    private final NotificationPort notificationPort;
    private final ClienteContactPort clienteContactPort;

    public PolizaEmitidaNotificationHandler(
            NotificationPort notificationPort, ClienteContactPort clienteContactPort) {
        this.notificationPort = notificationPort;
        this.clienteContactPort = clienteContactPort;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void manejar(PolizaEmitidaEvent event) {
        log.info(
                "Evento de póliza recibido AFTER_COMMIT eventId={} poliza={} cliente={}",
                event.eventId(),
                event.numeroPoliza(),
                event.clienteId());
        try {
            ClienteContactData contacto =
                    clienteContactPort
                            .buscarPorClienteId(event.clienteId())
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "No se encontró contacto del cliente"));
            if (contacto.telefono() == null || contacto.telefono().isBlank()) {
                log.warn(
                        "No se enviará la notificación de la póliza {}: cliente sin destinatario"
                                + " para el canal activo",
                        event.numeroPoliza());
                return;
            }

            NotificationResult resultado =
                    notificationPort.enviar(
                            new NotificationMessage(
                                    contacto.telefono(),
                                    NotificationType.POLIZA_EMITIDA,
                                    "Su póliza "
                                            + event.numeroPoliza()
                                            + " fue emitida correctamente.",
                                    Map.of(
                                            "numeroPoliza", event.numeroPoliza(),
                                            "nombreCliente", contacto.nombre(),
                                            "titulo", "Póliza emitida " + event.numeroPoliza())));
            log.info(
                    "Notificación finalizada eventId={} enviada={} identificador={} detalle={}",
                    event.eventId(),
                    resultado.enviada(),
                    resultado.identificador(),
                    resultado.detalle());
        } catch (Exception exception) {
            log.error(
                    "Póliza emitida, pero falló la notificación. poliza={}",
                    event.numeroPoliza(),
                    exception);
        }
    }
}
