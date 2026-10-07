package com.serenia.platform.iam.interfaces.rest.transform;

import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Combines the authenticated user, the opened session and the issued token into a
 * single {@link AuthenticatedUserResource}.
 */
public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, Session session, String token) {
        return new AuthenticatedUserResource(
                session.getId().value(),
                token,
                session.getExpiresAt(),
                UserResourceFromEntityAssembler.toResourceFromEntity(user));
    }
}
