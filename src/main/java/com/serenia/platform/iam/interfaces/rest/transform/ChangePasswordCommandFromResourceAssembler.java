package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.serenia.platform.iam.interfaces.rest.resources.ChangePasswordResource;

import java.util.UUID;

/**
 * Combines a {@link ChangePasswordResource} with the authenticated user and session
 * into a {@link ChangePasswordCommand}.
 */
public class ChangePasswordCommandFromResourceAssembler {
    public static ChangePasswordCommand toCommandFromResource(UUID userId, UUID sessionId, ChangePasswordResource resource) {
        return new ChangePasswordCommand(
                userId,
                sessionId,
                resource.currentPassword(),
                resource.newPassword());
    }
}
