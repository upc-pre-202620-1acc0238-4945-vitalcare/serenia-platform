package com.serenia.platform.shared.application.outboundservices.events;

import java.util.Collection;

/**
 * Outbound port for publishing domain events to the other bounded contexts.
 */
public interface DomainEventPublisher {

    /**
     * Publishes the given domain events in order.
     *
     * @param events the events to publish
     */
    void publish(Collection<?> events);
}
