package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.SocialReminderId;
import com.serenia.platform.socialcompanionship.domain.repositories.SocialReminderRepository;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.mappers.SocialReminderPersistenceMapper;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.repositories.SocialReminderJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link SocialReminderRepository} with Spring Data JPA.
 */
@Repository
public class SocialReminderRepositoryImpl implements SocialReminderRepository {

    private static final EnumSet<ReminderStatus> ACTIVE =
            EnumSet.of(ReminderStatus.SCHEDULED, ReminderStatus.PRESENTED, ReminderStatus.POSTPONED);
    private static final EnumSet<ReminderStatus> DUE_FOR_PRESENTATION =
            EnumSet.of(ReminderStatus.SCHEDULED, ReminderStatus.POSTPONED);
    private static final EnumSet<ReminderStatus> AWAITING_ACTION =
            EnumSet.of(ReminderStatus.PRESENTED, ReminderStatus.POSTPONED);

    private final SocialReminderJpaRepository socialReminderJpaRepository;

    public SocialReminderRepositoryImpl(SocialReminderJpaRepository socialReminderJpaRepository) {
        this.socialReminderJpaRepository = socialReminderJpaRepository;
    }

    @Override
    public SocialReminder save(SocialReminder socialReminder) {
        var saved = socialReminderJpaRepository.save(SocialReminderPersistenceMapper.toPersistenceFromDomain(socialReminder));
        return SocialReminderPersistenceMapper.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<SocialReminder> findById(SocialReminderId socialReminderId) {
        return socialReminderJpaRepository.findById(socialReminderId.value())
                .map(SocialReminderPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public List<SocialReminder> findActiveByOlderAdultId(OlderAdultId olderAdultId) {
        return socialReminderJpaRepository.findAllByOlderAdultIdAndStatusInOrderByRemindAtAsc(olderAdultId.value(), ACTIVE)
                .stream()
                .map(SocialReminderPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<SocialReminder> findAllDueForPresentation(Instant referenceTime) {
        return socialReminderJpaRepository.findAllByStatusInAndRemindAtLessThanEqual(DUE_FOR_PRESENTATION, referenceTime)
                .stream()
                .map(SocialReminderPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<SocialReminder> findAllAwaitingActionBefore(Instant referenceTime) {
        return socialReminderJpaRepository.findAllByStatusInAndRemindAtBefore(AWAITING_ACTION, referenceTime)
                .stream()
                .map(SocialReminderPersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
