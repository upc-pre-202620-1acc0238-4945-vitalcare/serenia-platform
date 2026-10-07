package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.commands.RegisterDistantRelativeCommand;
import com.serenia.platform.iam.interfaces.rest.resources.RegisterUserResource;

/** Converts a {@link RegisterUserResource} into a {@link RegisterDistantRelativeCommand}. */
public class RegisterDistantRelativeCommandFromResourceAssembler {
    public static RegisterDistantRelativeCommand toCommandFromResource(RegisterUserResource resource) {
        return new RegisterDistantRelativeCommand(
                resource.email(),
                resource.password(),
                resource.fullName(),
                resource.phoneNumber(),
                resource.birthDate(),
                resource.locale(),
                resource.timeZone());
    }
}
