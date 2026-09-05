package com.andinaseguros.interfaceadapters.out.notification;

import com.andinaseguros.usecases.port.out.notification.NotificationMessage;

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
