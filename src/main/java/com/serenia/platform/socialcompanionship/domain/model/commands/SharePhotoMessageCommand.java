package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of sharing the photo with the older adult.
 */
public record SharePhotoMessageCommand(UUID photoMessageId, UUID senderId) {
}
