package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the access of a relative to a care circle has been withdrawn.
 *
 * @param careCircleId the circle of the link
 * @param familyLinkId the identifier of the revoked link
 * @param relativeId   the relative who lost access
 * @param occurredAt   the instant of the revocation, in UTC
 */
public record FamilyLinkRevoked(UUID careCircleId, UUID familyLinkId, UUID relativeId, Instant occurredAt) {
}
