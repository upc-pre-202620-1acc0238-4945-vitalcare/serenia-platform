package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of modifying the content of an existing shared note.
 */
public record EditSharedNoteCommand(UUID sharedNoteId, UUID editorId, String content) {
}
