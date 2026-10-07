package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of a relative to play a received audio.
 */
public record PlayAudioMessageCommand(UUID audioMessageId, UUID relativeId) {
}
