package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of the older adult to see a received photo.
 */
public record ViewPhotoMessageCommand(UUID photoMessageId, UUID olderAdultId) {
}
