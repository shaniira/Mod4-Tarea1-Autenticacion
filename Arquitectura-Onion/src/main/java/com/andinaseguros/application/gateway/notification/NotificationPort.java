package com.andinaseguros.application.gateway.notification;

public interface NotificationPort {
    NotificationResult enviar(NotificationMessage message);
}
