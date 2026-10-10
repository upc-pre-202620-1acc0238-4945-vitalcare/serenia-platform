package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.socialcompanionship.domain.model.commands.SelectPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.SelectPhotoMessageResource;

import java.util.UUID;

/**
 * Combines the received file with the circle and the authenticated relative into a
 * {@link SelectPhotoMessageCommand}.
 */
public class SelectPhotoMessageCommandFromResourceAssembler {
    public static SelectPhotoMessageCommand toCommandFromResource(UUID careCircleId, UUID senderId,
                                                                  SelectPhotoMessageResource resource) {
        return new SelectPhotoMessageCommand(careCircleId, senderId,
                MediaFileFromMultipartFileAssembler.toMediaFile(resource.file()));
    }
}
