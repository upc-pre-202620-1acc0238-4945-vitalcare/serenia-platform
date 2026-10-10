package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when an invitation code ended its validity without being redeemed.
 *
 * @param careCircleId     the circle the code belonged to
 * @param invitationCodeId the identifier of the expired code
 * @param occurredAt       the instant the code was marked as expired, in UTC
 */
public record InvitationCodeExpired(UUID careCircleId, UUID invitationCodeId, Instant occurredAt) {
}
