package com.serenia.platform.carecircle.domain.model.queries;

import java.util.UUID;

/**
 * Query of the relatives with an active link in a care circle.
 */
public record GetActiveFamilyLinksByCareCircleIdQuery(UUID careCircleId, UUID requesterId) {
}
