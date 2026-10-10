package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.commands.RegisterOlderAdultCommand;
import com.serenia.platform.iam.interfaces.rest.resources.RegisterUserResource;

/** Converts a {@link RegisterUserResource} into a {@link RegisterOlderAdultCommand}. */
public class RegisterOlderAdultCommandFromResourceAssembler {
    public static RegisterOlderAdultCommand toCommandFromResource(RegisterUserResource resource) {
        return new RegisterOlderAdultCommand(
                resource.email(),
                resource.password(),
                resource.fullName(),
                resource.phoneNumber(),
                resource.birthDate(),
                resource.locale(),
                resource.timeZone());
    }
}
