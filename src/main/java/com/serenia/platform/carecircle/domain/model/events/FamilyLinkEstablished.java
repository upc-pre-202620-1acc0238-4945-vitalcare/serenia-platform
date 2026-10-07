package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a relative has been linked, or linked again, to a care circle.
 *
 * @param careCircleId the circle of the link
 * @param familyLinkId the identifier of the link
 * @param relativeId   the linked relative
 * @param occurredAt   the instant the link became active, in UTC
 */
public record FamilyLinkEstablished(UUID careCircleId, UUID familyLinkId, UUID relativeId, Instant occurredAt) {
}
