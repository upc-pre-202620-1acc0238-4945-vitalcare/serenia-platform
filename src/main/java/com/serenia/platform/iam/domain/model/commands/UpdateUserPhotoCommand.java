package com.serenia.platform.iam.domain.model.commands;

import java.util.UUID;

/**
 * Intention of updating the profile photo of a user.
 */
public record UpdateUserPhotoCommand(UUID userId, String photoUrl) {
}
