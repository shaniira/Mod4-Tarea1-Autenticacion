package com.andinaseguros.usecases.port.out.notification;

public interface NotificationPort {
    NotificationResult enviar(NotificationMessage message);
}
