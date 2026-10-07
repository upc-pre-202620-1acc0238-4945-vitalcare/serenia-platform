package com.serenia.platform.socialcompanionship.domain.model.commands;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import java.util.UUID;

/**
 * Intention of the older adult to save a recorded audio as a draft.
 */
public record RecordAudioMessageCommand(UUID senderId, MediaFile audioFile, int durationSeconds) {
}
