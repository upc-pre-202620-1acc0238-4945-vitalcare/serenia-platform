package com.serenia.platform.socialcompanionship.domain.repositories;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.SocialReminderId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link SocialReminder} aggregate.
 */
public interface SocialReminderRepository {

    SocialReminder save(SocialReminder socialReminder);

    Optional<SocialReminder> findById(SocialReminderId socialReminderId);

    /** Returns the scheduled, presented or postponed reminders of the older adult, ordered by time. */
    List<SocialReminder> findActiveByOlderAdultId(OlderAdultId olderAdultId);

    /** Returns the scheduled or postponed reminders whose time arrived. */
    List<SocialReminder> findAllDueForPresentation(Instant referenceTime);

    /** Returns the presented or postponed reminders whose time is before the given instant. */
    List<SocialReminder> findAllAwaitingActionBefore(Instant referenceTime);
}
