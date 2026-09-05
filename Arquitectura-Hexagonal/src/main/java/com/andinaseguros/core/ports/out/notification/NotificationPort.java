package com.andinaseguros.core.ports.out.notification;

public interface NotificationPort {
    NotificationResult enviar(NotificationMessage message);
}
