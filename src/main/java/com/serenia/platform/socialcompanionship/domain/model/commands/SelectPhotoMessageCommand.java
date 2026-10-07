package com.serenia.platform.socialcompanionship.domain.model.commands;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import java.util.UUID;

/**
 * Intention of a relative to upload a photo as a draft.
 */
public record SelectPhotoMessageCommand(UUID careCircleId, UUID senderId, MediaFile photoFile) {
}
