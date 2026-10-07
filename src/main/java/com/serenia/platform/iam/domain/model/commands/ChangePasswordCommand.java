package com.serenia.platform.iam.domain.model.commands;

import java.util.UUID;

/**
 * Intention of replacing a user's password after verifying the current one.
 *
 * @param userId          the user whose password changes
 * @param sessionId       the session from which the change is requested; it is preserved
 * @param currentPassword the raw current password
 * @param newPassword     the raw new password
 */
public record ChangePasswordCommand(UUID userId, UUID sessionId, String currentPassword, String newPassword) {
}
