package com.serenia.platform.carecircle.domain.model.queries;

import java.util.UUID;

/**
 * Query of the shared notes of a care circle, from the most recent to the oldest.
 */
public record GetSharedNotesByCareCircleIdQuery(UUID careCircleId, UUID requesterId) {
}
