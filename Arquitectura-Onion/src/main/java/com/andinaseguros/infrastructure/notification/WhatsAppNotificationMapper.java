package com.andinaseguros.infrastructure.notification;

import com.andinaseguros.application.gateway.notification.NotificationMessage;

public class WhatsAppNotificationMapper {
    WhatsAppTextRequest toRequest(NotificationMessage message) {
        String number =
                message.destinatario() == null
                        ? ""
                        : message.destinatario().replaceAll("[^0-9]", "");
        if (number.isBlank())
            throw new IllegalArgumentException("El teléfono de destino es obligatorio");
        return new WhatsAppTextRequest(number, message.contenido());
    }
}
