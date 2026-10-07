package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of discarding a photo before sharing it.
 */
public record DiscardPhotoMessageCommand(UUID photoMessageId, UUID senderId) {
}
