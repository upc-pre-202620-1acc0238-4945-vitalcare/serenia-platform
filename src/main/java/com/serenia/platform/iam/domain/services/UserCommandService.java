package com.serenia.platform.iam.domain.services;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.serenia.platform.iam.domain.model.commands.RegisterDistantRelativeCommand;
import com.serenia.platform.iam.domain.model.commands.RegisterOlderAdultCommand;
import com.serenia.platform.iam.domain.model.commands.UpdateProfileDataCommand;
import com.serenia.platform.iam.domain.model.commands.UpdateUserPhotoCommand;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on user accounts.
 *
 * <p>Every handler returns a {@link Result} so callers can branch on expected
 * business failures (e.g. duplicate email) without relying on exceptions.</p>
 */
public interface UserCommandService {

    /** Registers an account with the older adult role. */
    Result<User, ApplicationError> handle(RegisterOlderAdultCommand command);

    /** Registers an account with the distant relative role. */
    Result<User, ApplicationError> handle(RegisterDistantRelativeCommand command);

    /** Replaces the profile photo of an account. */
    Result<User, ApplicationError> handle(UpdateUserPhotoCommand command);

    /** Replaces the personal data of an account. */
    Result<User, ApplicationError> handle(UpdateProfileDataCommand command);

    /** Changes the password after verifying the current one. */
    Result<Void, ApplicationError> handle(ChangePasswordCommand command);
}
