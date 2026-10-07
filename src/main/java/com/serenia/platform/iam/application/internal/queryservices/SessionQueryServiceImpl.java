package com.serenia.platform.iam.application.internal.queryservices;

import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.queries.GetActiveSessionByIdQuery;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.repositories.SessionRepository;
import com.serenia.platform.iam.domain.services.SessionQueryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Application service that returns a {@link Session} only while it is still active.
 */
@Service
public class SessionQueryServiceImpl implements SessionQueryService {

    private final SessionRepository sessionRepository;

    public SessionQueryServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Optional<Session> handle(GetActiveSessionByIdQuery query) {
        var now = Instant.now();
        return sessionRepository.findById(new SessionId(query.sessionId()))
                .filter(session -> session.isActive(now));
    }
}
