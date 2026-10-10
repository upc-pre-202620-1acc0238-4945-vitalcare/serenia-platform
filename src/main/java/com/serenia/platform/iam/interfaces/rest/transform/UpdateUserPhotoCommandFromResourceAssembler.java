package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.commands.UpdateUserPhotoCommand;
import com.serenia.platform.iam.interfaces.rest.resources.UpdateUserPhotoResource;

import java.util.UUID;

/**
 * Converts an {@link UpdateUserPhotoResource} (plus the URL path variable)
 * into an {@link UpdateUserPhotoCommand}.
 */
public class UpdateUserPhotoCommandFromResourceAssembler {
    public static UpdateUserPhotoCommand toCommandFromResource(UUID userId, UpdateUserPhotoResource resource) {
        return new UpdateUserPhotoCommand(userId, resource.photoUrl());
    }
}
