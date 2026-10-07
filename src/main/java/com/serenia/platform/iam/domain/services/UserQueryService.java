package com.serenia.platform.iam.domain.services;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.queries.GetUserByIdQuery;

import java.util.Optional;

/**
 * Contract of the read operations on user accounts.
 */
public interface UserQueryService {

    /** Finds an account by its identifier. */
    Optional<User> handle(GetUserByIdQuery query);
}
