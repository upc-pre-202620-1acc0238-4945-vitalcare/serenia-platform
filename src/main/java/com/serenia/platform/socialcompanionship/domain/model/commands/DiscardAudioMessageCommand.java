package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of discarding an audio before sharing it.
 */
public record DiscardAudioMessageCommand(UUID audioMessageId, UUID senderId) {
}
