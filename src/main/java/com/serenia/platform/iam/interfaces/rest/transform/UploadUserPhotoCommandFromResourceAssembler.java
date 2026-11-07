package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.commands.UploadUserPhotoCommand;
import com.serenia.platform.iam.interfaces.rest.resources.UploadUserPhotoResource;
import com.serenia.platform.shared.interfaces.rest.transform.MediaFileFromMultipartFileAssembler;

import java.util.UUID;

/**
 * Combines the received image with the URL path variable into an {@link UploadUserPhotoCommand}.
 */
public class UploadUserPhotoCommandFromResourceAssembler {
    public static UploadUserPhotoCommand toCommandFromResource(UUID userId, UploadUserPhotoResource resource) {
        return new UploadUserPhotoCommand(userId, MediaFileFromMultipartFileAssembler.toMediaFile(resource.file()));
    }
}
