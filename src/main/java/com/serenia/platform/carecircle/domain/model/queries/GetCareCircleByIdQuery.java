package com.serenia.platform.carecircle.domain.model.queries;

import java.util.UUID;

/**
 * Query of a care circle by its identifier, on behalf of the requester.
 */
public record GetCareCircleByIdQuery(UUID careCircleId, UUID requesterId) {
}
