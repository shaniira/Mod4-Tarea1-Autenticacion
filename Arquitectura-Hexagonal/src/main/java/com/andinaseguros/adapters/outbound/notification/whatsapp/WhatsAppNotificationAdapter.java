package com.andinaseguros.adapters.outbound.notification.whatsapp;

import com.andinaseguros.core.ports.out.notification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhatsAppNotificationAdapter implements NotificationPort {
    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationAdapter.class);
    private final WhatsAppClient client;
    private final WhatsAppNotificationMapper mapper;

    public WhatsAppNotificationAdapter(WhatsAppClient client, WhatsAppNotificationMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public NotificationResult enviar(NotificationMessage message) {
        log.info(
                "Iniciando envío WhatsApp tipo={} destinatario={}",
                message.tipo(),
                enmascarar(message.destinatario()));
        try {
            WhatsAppTextRequest request = mapper.toRequest(message);
            WhatsAppTextResponse response = client.enviarTexto(request);
            if (response == null || !response.success()) {
                String detalle = response == null ? "Respuesta vacía" : response.message();
                log.warn(
                        "WhatsApp rechazado tipo={} destinatario={} detalle={}",
                        message.tipo(),
                        enmascarar(message.destinatario()),
                        detalle);
                return new NotificationResult(false, "WHATSAPP", detalle);
            }
            log.info(
                    "WhatsApp enviado tipo={} destinatario={} detalle={}",
                    message.tipo(),
                    enmascarar(message.destinatario()),
                    response.message());
            return new NotificationResult(true, "WHATSAPP", response.message());
        } catch (RuntimeException e) {
            log.error(
                    "Error enviando WhatsApp tipo={} destinatario={} causa={} mensaje={}",
                    message.tipo(),
                    enmascarar(message.destinatario()),
                    e.getClass().getSimpleName(),
                    e.getMessage());
            return new NotificationResult(false, "WHATSAPP", "WHATSAPP_UNAVAILABLE");
        }
    }

    private static String enmascarar(String numero) {
        if (numero == null || numero.isBlank()) return "sin-numero";
        String limpio = numero.replaceAll("[^0-9]", "");
        int visibles = Math.min(4, limpio.length());
        return "***" + limpio.substring(limpio.length() - visibles);
    }
}
