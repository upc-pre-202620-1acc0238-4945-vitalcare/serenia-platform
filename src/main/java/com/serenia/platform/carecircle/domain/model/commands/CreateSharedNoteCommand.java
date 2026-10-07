package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of writing a shared note in a care circle.
 */
public record CreateSharedNoteCommand(UUID careCircleId, UUID authorId, String content) {
}
