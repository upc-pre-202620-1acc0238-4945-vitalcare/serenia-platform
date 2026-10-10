package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.dailycheckin.domain.exceptions.CheckInAlreadyOpenedException;
import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInStatus;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInRepository;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.mappers.CheckInPersistenceMapper;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories.CheckInJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link CheckInRepository} with Spring Data JPA.
 *
 * <p>If two runs try to open the same day, the violation of the unique index is translated into
 * a {@link CheckInAlreadyOpenedException}, so opening is idempotent.</p>
 */
@Repository
public class CheckInRepositoryImpl implements CheckInRepository {

    private final CheckInJpaRepository checkInJpaRepository;

    public CheckInRepositoryImpl(CheckInJpaRepository checkInJpaRepository) {
        this.checkInJpaRepository = checkInJpaRepository;
    }

    @Override
    public CheckIn save(CheckIn checkIn) {
        try {
            // Flush immediately so a unique index violation surfaces here and not at commit time
            var saved = checkInJpaRepository.saveAndFlush(CheckInPersistenceMapper.toPersistenceFromDomain(checkIn));
            return CheckInPersistenceMapper.toDomainFromPersistence(saved);
        } catch (DataIntegrityViolationException e) {
            throw new CheckInAlreadyOpenedException();
        }
    }

    @Override
    public Optional<CheckIn> findById(CheckInId checkInId) {
        return checkInJpaRepository.findById(checkInId.value()).map(CheckInPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public Optional<CheckIn> findByOlderAdultIdAndCheckDate(OlderAdultId olderAdultId, LocalDate checkDate) {
        return checkInJpaRepository.findByOlderAdultIdAndCheckDate(olderAdultId.value(), checkDate)
                .map(CheckInPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public boolean existsByOlderAdultIdAndCheckDate(OlderAdultId olderAdultId, LocalDate checkDate) {
        return checkInJpaRepository.existsByOlderAdultIdAndCheckDate(olderAdultId.value(), checkDate);
    }

    @Override
    public List<CheckIn> findAllByOlderAdultIdAndCheckDateBetween(OlderAdultId olderAdultId, LocalDate fromDate,
                                                                 LocalDate toDate) {
        return checkInJpaRepository
                .findAllByOlderAdultIdAndCheckDateBetweenOrderByCheckDateAsc(olderAdultId.value(), fromDate, toDate)
                .stream()
                .map(CheckInPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<CheckIn> findAllPendingDueForPrompt(Instant referenceTime) {
        return checkInJpaRepository.findAllByStatusDueForPrompt(CheckInStatus.PENDING, referenceTime).stream()
                .map(CheckInPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<CheckIn> findAllPendingPastDeadline(Instant referenceTime) {
        return checkInJpaRepository.findAllByStatusAndDeadlineAtLessThanEqual(CheckInStatus.PENDING, referenceTime).stream()
                .map(CheckInPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<CheckInQuestionId> findRecentQuestionIds(OlderAdultId olderAdultId, int limit) {
        if (limit <= 0) return List.of();
        return checkInJpaRepository.findRecentQuestionIds(olderAdultId.value(), PageRequest.of(0, limit)).stream()
                .map(CheckInQuestionId::new)
                .toList();
    }
}
