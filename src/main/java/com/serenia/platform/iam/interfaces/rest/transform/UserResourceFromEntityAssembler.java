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
                photoUrlOf(user),
                user.getLocale().value(),
                user.getTimeZone().getId(),
                user.getStatus().name());
    }

    /**
     * Returns the protected endpoint of an uploaded photo, versioned by the last update so a
     * new photo is not served from a client cache, or the external URL otherwise.
     */
    private static String photoUrlOf(User user) {
        if (!user.hasUploadedPhoto()) return user.getPhotoUrl();
        return PHOTO_ENDPOINT.formatted(user.getId().value(), user.getUpdatedAt().toEpochMilli());
    }

    private static final String PHOTO_ENDPOINT = "/api/v1/users/%s/photo?v=%d";
}
