package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderStatus;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities.SocialReminderPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link SocialReminderPersistenceEntity}.
 *
 * <p>The active reminders of an older adult run over the {@code (older_adult_id, remind_at)}
 * index; the reminders to present or close, over the {@code (status, remind_at)} index.</p>
 */
@Repository
public interface SocialReminderJpaRepository extends JpaRepository<SocialReminderPersistenceEntity, UUID> {

    List<SocialReminderPersistenceEntity> findAllByOlderAdultIdAndStatusInOrderByRemindAtAsc(
            UUID olderAdultId, Collection<ReminderStatus> statuses);

    List<SocialReminderPersistenceEntity> findAllByStatusInAndRemindAtLessThanEqual(
            Collection<ReminderStatus> statuses, Instant referenceTime);

    List<SocialReminderPersistenceEntity> findAllByStatusInAndRemindAtBefore(
            Collection<ReminderStatus> statuses, Instant referenceTime);
}
