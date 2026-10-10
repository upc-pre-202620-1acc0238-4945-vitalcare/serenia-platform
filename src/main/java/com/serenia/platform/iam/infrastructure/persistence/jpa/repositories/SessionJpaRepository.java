package com.serenia.platform.iam.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.iam.infrastructure.persistence.jpa.entities.SessionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link SessionPersistenceEntity}.
 */
@Repository
public interface SessionJpaRepository extends JpaRepository<SessionPersistenceEntity, UUID> {

    /** Returns the sessions of a user that are neither revoked nor expired at the reference time. */
    @Query("""
            select s from SessionPersistenceEntity s
            where s.userId = :userId
              and s.revokedAt is null
              and s.expiresAt > :referenceTime
            """)
    List<SessionPersistenceEntity> findActiveByUserId(@Param("userId") UUID userId,
                                                      @Param("referenceTime") Instant referenceTime);
}
