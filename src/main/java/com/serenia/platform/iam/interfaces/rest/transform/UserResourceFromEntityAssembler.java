package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.interfaces.rest.resources.UserResource;

/** Converts a {@link User} aggregate into its {@link UserResource} REST representation. */
public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(
                user.getId().value(),
                user.getEmail().value(),
                user.getRole().name(),
                user.getFullName().value(),
                user.getPhoneNumber() == null ? null : user.getPhoneNumber().value(),
                user.getBirthDate(),
                user.getPhotoUrl(),
                user.getLocale().value(),
                user.getTimeZone().getId(),
                user.getStatus().name());
    }
}
