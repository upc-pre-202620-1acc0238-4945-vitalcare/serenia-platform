package com.serenia.platform.carecircle.domain.model.queries;

import java.util.UUID;

/**
 * Query of the invitation codes of a care circle that can still be redeemed.
 */
public record GetPendingInvitationCodesByCareCircleIdQuery(UUID careCircleId, UUID requesterId) {
}
