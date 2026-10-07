package com.serenia.platform.socialcompanionship.domain.model.queries;

import java.util.UUID;

/**
 * Query of the shared audios of a circle, from the most recent to the oldest.
 */
public record GetSharedAudioMessagesByCareCircleIdQuery(UUID careCircleId, UUID requesterId) {
}
