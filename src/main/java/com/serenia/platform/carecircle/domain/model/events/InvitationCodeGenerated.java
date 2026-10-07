package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when an invitation code has been generated.
 *
 * @param careCircleId     the circle the code invites to
 * @param invitationCodeId the identifier of the code
 * @param expiresAt        the instant the code stops being redeemable, in UTC
 * @param occurredAt       the instant the code was generated, in UTC
 */
public record InvitationCodeGenerated(UUID careCircleId, UUID invitationCodeId, Instant expiresAt, Instant occurredAt) {
}
