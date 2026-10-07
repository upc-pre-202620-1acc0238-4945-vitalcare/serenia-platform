package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities.SmallWinPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link SmallWinPersistenceEntity}.
 *
 * <p>The queries by period are resolved over the {@code (older_adult_id, recorded_at)} index.</p>
 */
@Repository
public interface SmallWinJpaRepository extends JpaRepository<SmallWinPersistenceEntity, UUID> {

    /** Returns the small wins recorded from {@code from}, inclusive, to {@code to}, exclusive. */
    @Query("""
            select s from SmallWinPersistenceEntity s
            where s.olderAdultId = :olderAdultId
              and s.recordedAt >= :from
              and s.recordedAt < :to
            order by s.recordedAt asc
            """)
    List<SmallWinPersistenceEntity> findAllByOlderAdultIdRecordedBetween(@Param("olderAdultId") UUID olderAdultId,
                                                                        @Param("from") Instant from,
                                                                        @Param("to") Instant to);
}
