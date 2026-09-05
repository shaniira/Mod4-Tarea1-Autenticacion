package com.andinaseguros.infrastructure.event;

import com.andinaseguros.domain.event.DomainEvent;
import com.andinaseguros.application.gateway.event.DomainEventPublisherPort;
import org.slf4j.*;
import org.springframework.context.ApplicationEventPublisher;

public class SpringDomainEventPublisherAdapter implements DomainEventPublisherPort {
    private static final Logger log =
            LoggerFactory.getLogger(SpringDomainEventPublisherAdapter.class);
    private final ApplicationEventPublisher publisher;

    public SpringDomainEventPublisherAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publicar(DomainEvent event) {
        log.debug(
                "Publicando evento tipo={} eventId={} occurredAt={}",
                event.getClass().getSimpleName(),
                event.eventId(),
                event.occurredAt());
        publisher.publishEvent(event);
    }
}
