package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.commands.SignInCommand;
import com.serenia.platform.iam.interfaces.rest.resources.SignInResource;

/** Converts a {@link SignInResource} into a {@link SignInCommand}. */
public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password(), resource.deviceInfo());
    }
}
