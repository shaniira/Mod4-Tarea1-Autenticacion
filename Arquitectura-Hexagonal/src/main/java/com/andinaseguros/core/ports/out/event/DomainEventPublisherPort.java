package com.andinaseguros.core.ports.out.event;

import com.andinaseguros.core.domain.event.DomainEvent;

public interface DomainEventPublisherPort {
    void publicar(DomainEvent event);
}
