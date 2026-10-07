package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a shared note has been created.
 *
 * @param sharedNoteId the identifier of the note
 * @param careCircleId the circle the note belongs to
 * @param authorId     the relative who wrote the note
 * @param occurredAt   the instant of the creation, in UTC
 */
public record SharedNoteCreated(UUID sharedNoteId, UUID careCircleId, UUID authorId, Instant occurredAt) {
}
