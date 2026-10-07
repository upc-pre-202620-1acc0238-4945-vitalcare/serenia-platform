package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInStatus;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.CheckInPersistenceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CheckInPersistenceEntity}.
 *
 * <p>The queries by older adult and date use the unique {@code (older_adult_id, check_date)}
 * index. The searches of check-ins to prompt or close use the {@code (status, deadline_at)}
 * index; since each older adult has at most one pending check-in, the set they scan is small.</p>
 */
@Repository
public interface CheckInJpaRepository extends JpaRepository<CheckInPersistenceEntity, UUID> {

    Optional<CheckInPersistenceEntity> findByOlderAdultIdAndCheckDate(UUID olderAdultId, LocalDate checkDate);

    boolean existsByOlderAdultIdAndCheckDate(UUID olderAdultId, LocalDate checkDate);

    List<CheckInPersistenceEntity> findAllByOlderAdultIdAndCheckDateBetweenOrderByCheckDateAsc(
            UUID olderAdultId, LocalDate fromDate, LocalDate toDate);

    @Query("""
            select c from CheckInPersistenceEntity c
            where c.status = :status
              and c.deadlineAt > :referenceTime
              and c.promptedAt is null
              and c.scheduledFor <= :referenceTime
            """)
    List<CheckInPersistenceEntity> findAllByStatusDueForPrompt(@Param("status") CheckInStatus status,
                                                             @Param("referenceTime") Instant referenceTime);

    List<CheckInPersistenceEntity> findAllByStatusAndDeadlineAtLessThanEqual(CheckInStatus status, Instant referenceTime);

    @Query("""
            select c.questionId from CheckInPersistenceEntity c
            where c.olderAdultId = :olderAdultId
            order by c.checkDate desc
            """)
    List<UUID> findRecentQuestionIds(@Param("olderAdultId") UUID olderAdultId, Pageable pageable);
}
