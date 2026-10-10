package com.serenia.platform.dailycheckin.domain.repositories;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link CheckIn} aggregate.
 */
public interface CheckInRepository {

    /**
     * Persists the check-in.
     *
     * @throws com.serenia.platform.dailycheckin.domain.exceptions.CheckInAlreadyOpenedException
     *         when the older adult already has a check-in for the same date
     */
    CheckIn save(CheckIn checkIn);

    Optional<CheckIn> findById(CheckInId checkInId);

    Optional<CheckIn> findByOlderAdultIdAndCheckDate(OlderAdultId olderAdultId, LocalDate checkDate);

    boolean existsByOlderAdultIdAndCheckDate(OlderAdultId olderAdultId, LocalDate checkDate);

    /** Returns the check-ins between both dates, inclusive, ordered by date. */
    List<CheckIn> findAllByOlderAdultIdAndCheckDateBetween(OlderAdultId olderAdultId, LocalDate fromDate, LocalDate toDate);

    /** Returns the pending check-ins, not prompted yet, whose presentation time arrived and deadline has not passed. */
    List<CheckIn> findAllPendingDueForPrompt(Instant referenceTime);

    /** Returns the pending check-ins whose deadline has passed. */
    List<CheckIn> findAllPendingPastDeadline(Instant referenceTime);

    /** Returns the questions of the latest check-ins of the older adult, the most recent first. */
    List<CheckInQuestionId> findRecentQuestionIds(OlderAdultId olderAdultId, int limit);
}
