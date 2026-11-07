package com.serenia.platform.iam.domain.services;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.serenia.platform.iam.domain.model.queries.GetUserPhotoQuery;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.shared.domain.model.valueobjects.MediaFile;

import java.util.Optional;

/**
 * Contract of the read operations on user accounts.
 */
public interface UserQueryService {

    /** Finds an account by its identifier. */
    Optional<User> handle(GetUserByIdQuery query);

    /** Loads the uploaded profile photo of an account. */
    Result<MediaFile, ApplicationError> handle(GetUserPhotoQuery query);
}
