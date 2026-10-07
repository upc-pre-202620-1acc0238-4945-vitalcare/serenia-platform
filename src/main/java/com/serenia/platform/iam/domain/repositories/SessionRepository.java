package com.serenia.platform.iam.domain.repositories;

import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link Session} aggregate.
 *
 * <p>Free of any JPA or Spring Data dependency; the infrastructure layer provides
 * the implementation.</p>
 */
public interface SessionRepository {

    Session save(Session session);

    List<Session> saveAll(List<Session> sessions);

    Optional<Session> findById(SessionId sessionId);

    /** Returns the sessions of the user that are neither revoked nor expired at the reference time. */
    List<Session> findActiveByUserId(UserId userId, Instant referenceTime);
}
