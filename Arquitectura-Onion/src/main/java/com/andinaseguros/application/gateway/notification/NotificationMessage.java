package com.andinaseguros.application.gateway.notification;

import java.util.Map;

public record NotificationMessage(
        String destinatario,
        NotificationType tipo,
        String contenido,
        Map<String, String> parametros) {
    public NotificationMessage {
        parametros = parametros == null ? Map.of() : Map.copyOf(parametros);
    }
}
