package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a relative has redeemed a valid invitation code.
 *
 * <p>Handled in the same transaction to establish the family link of the relative.</p>
 *
 * @param careCircleId      the circle the relative joins
 * @param invitationCodeId  the identifier of the redeemed code
 * @param relativeId        the relative who redeemed the code
 * @param relationshipLabel the relationship declared by the relative, possibly {@code null}
 * @param occurredAt        the instant of the redemption, in UTC
 */
public record InvitationCodeRedeemed(UUID careCircleId, UUID invitationCodeId, UUID relativeId,
                                     String relationshipLabel, Instant occurredAt) {
}
