package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of sharing the audio with the linked relatives.
 */
public record ShareAudioMessageCommand(UUID audioMessageId, UUID senderId) {
}
