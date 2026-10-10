package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.socialcompanionship.domain.model.commands.RecordAudioMessageCommand;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.RecordAudioMessageResource;

import java.util.UUID;

/**
 * Combines the received file with the authenticated older adult into a {@link RecordAudioMessageCommand}.
 */
public class RecordAudioMessageCommandFromResourceAssembler {
    public static RecordAudioMessageCommand toCommandFromResource(UUID senderId, RecordAudioMessageResource resource) {
        return new RecordAudioMessageCommand(
                senderId,
                MediaFileFromMultipartFileAssembler.toMediaFile(resource.file()),
                resource.durationSeconds());
    }
}
