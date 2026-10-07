package com.serenia.platform.iam.application.internal.outboundservices.events;

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
