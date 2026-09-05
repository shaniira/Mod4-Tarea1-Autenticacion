package com.andinaseguros.application.gateway.event;

import com.andinaseguros.domain.event.DomainEvent;

public interface DomainEventPublisherPort {
    void publicar(DomainEvent event);
}
