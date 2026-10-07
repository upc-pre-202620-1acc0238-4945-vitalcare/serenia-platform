package com.serenia.platform.iam.infrastructure.messaging.publishers;

import com.serenia.platform.iam.application.internal.outboundservices.events.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Publishes domain events inside the modular monolith through Spring's
 * {@link ApplicationEventPublisher}, without an external message broker.
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(Collection<?> events) {
        events.forEach(applicationEventPublisher::publishEvent);
    }
}
