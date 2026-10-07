package com.serenia.platform.iam.domain.services;

import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.queries.GetActiveSessionByIdQuery;

import java.util.Optional;

/**
 * Contract of the read operations on active sessions.
 */
public interface SessionQueryService {

    /** Returns the session only if it is still active at the time of the query. */
    Optional<Session> handle(GetActiveSessionByIdQuery query);
}
