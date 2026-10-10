package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.commands.UpdateProfileDataCommand;
import com.serenia.platform.iam.interfaces.rest.resources.UpdateProfileDataResource;

import java.util.UUID;

/**
 * Converts an {@link UpdateProfileDataResource} (plus the URL path variable)
 * into an {@link UpdateProfileDataCommand}.
 */
public class UpdateProfileDataCommandFromResourceAssembler {
    public static UpdateProfileDataCommand toCommandFromResource(UUID userId, UpdateProfileDataResource resource) {
        return new UpdateProfileDataCommand(
                userId,
                resource.fullName(),
                resource.phoneNumber(),
                resource.birthDate(),
                resource.locale(),
                resource.timeZone());
    }
}
