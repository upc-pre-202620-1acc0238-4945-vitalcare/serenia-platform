package com.serenia.platform.iam.domain.model.commands;

import com.serenia.platform.shared.domain.model.valueobjects.MediaFile;

import java.util.UUID;

/**
 * Intention of replacing the profile photo of a user with an uploaded image.
 *
 * @param userId    the identifier of the account
 * @param photoFile the JPEG or PNG image received
 */
public record UploadUserPhotoCommand(UUID userId, MediaFile photoFile) {
}
