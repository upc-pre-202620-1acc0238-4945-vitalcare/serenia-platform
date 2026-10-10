package com.serenia.platform.iam.domain.services;

import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.commands.RevokeSessionsCommand;
import com.serenia.platform.iam.domain.model.commands.SignInCommand;
import com.serenia.platform.iam.domain.model.commands.SignOutCommand;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.List;

/**
 * Contract of the authentication, sign-out and revocation of sessions.
 */
public interface SessionCommandService {

    /**
     * Authenticates the user and opens a session.
     *
     * @return the authenticated user, the opened session and the issued token
     */
    Result<ImmutableTriple<User, Session, String>, ApplicationError> handle(SignInCommand command);

    /** Closes an active session of the requesting user. */
    Result<Void, ApplicationError> handle(SignOutCommand command);

    /**
     * Revokes the active sessions of the user except the preserved one.
     *
     * @return the identifiers of the revoked sessions
     */
    Result<List<SessionId>, ApplicationError> handle(RevokeSessionsCommand command);
}
