package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the content of a shared note has been modified.
 *
 * @param sharedNoteId the identifier of the note
 * @param occurredAt   the instant of the edition, in UTC
 */
public record SharedNoteEdited(UUID sharedNoteId, Instant occurredAt) {
}
