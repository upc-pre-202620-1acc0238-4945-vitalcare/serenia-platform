package com.serenia.platform.socialcompanionship.domain.model.queries;

import java.util.UUID;

/**
 * Query of the shared photos of a circle, from the most recent to the oldest.
 */
public record GetSharedPhotoMessagesByCareCircleIdQuery(UUID careCircleId, UUID requesterId) {
}
