package com.serenia.platform.iam.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.domain.repositories.SessionRepository;
import com.serenia.platform.iam.infrastructure.persistence.jpa.mappers.SessionPersistenceMapper;
import com.serenia.platform.iam.infrastructure.persistence.jpa.repositories.SessionJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link SessionRepository} with Spring Data JPA.
 */
@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final SessionJpaRepository sessionJpaRepository;

    public SessionRepositoryImpl(SessionJpaRepository sessionJpaRepository) {
        this.sessionJpaRepository = sessionJpaRepository;
    }

    @Override
    public Session save(Session session) {
        var saved = sessionJpaRepository.save(SessionPersistenceMapper.toPersistenceFromDomain(session));
        return SessionPersistenceMapper.toDomainFromPersistence(saved);
    }

    @Override
    public List<Session> saveAll(List<Session> sessions) {
        var entities = sessions.stream()
                .map(SessionPersistenceMapper::toPersistenceFromDomain)
                .toList();
        return sessionJpaRepository.saveAll(entities).stream()
                .map(SessionPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Session> findById(SessionId sessionId) {
        return sessionJpaRepository.findById(sessionId.value())
                .map(SessionPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public List<Session> findActiveByUserId(UserId userId, Instant referenceTime) {
        return sessionJpaRepository.findActiveByUserId(userId.value(), referenceTime).stream()
                .map(SessionPersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
