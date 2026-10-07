package com.serenia.platform.socialcompanionship.domain.model.aggregates;

import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.serenia.platform.socialcompanionship.domain.exceptions.InvalidReminderStateException;
import com.serenia.platform.socialcompanionship.domain.exceptions.NoTimeLeftTodayException;
import com.serenia.platform.socialcompanionship.domain.model.events.SocialReminderCanceled;
import com.serenia.platform.socialcompanionship.domain.model.events.SocialReminderCompleted;
import com.serenia.platform.socialcompanionship.domain.model.events.SocialReminderMarkedAsMissed;
import com.serenia.platform.socialcompanionship.domain.model.events.SocialReminderPostponed;
import com.serenia.platform.socialcompanionship.domain.model.events.SocialReminderPresented;
import com.serenia.platform.socialcompanionship.domain.model.events.SocialReminderScheduled;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderDescription;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderTitle;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.SocialReminderId;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate root representing a social-contact reminder scheduled by the older adult, such as
 * calling a friend or attending an activity.
 *
 * <p>Controls its presentation, postponement, completion, cancellation and closing as missed.</p>
 */
@Getter
public class SocialReminder extends AbstractDomainAggregateRoot<SocialReminder> {

    /** Time a presented reminder is postponed by. */
    public static final Duration POSTPONEMENT = Duration.ofMinutes(60);

    private static final Set<ReminderStatus> ACTIVE = EnumSet.of(
            ReminderStatus.SCHEDULED, ReminderStatus.PRESENTED, ReminderStatus.POSTPONED);
    private static final Set<ReminderStatus> AWAITING_ACTION = EnumSet.of(ReminderStatus.PRESENTED, ReminderStatus.POSTPONED);

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "social.reminder.required.value";
    private static final String REMIND_AT_IN_PAST_MESSAGE_KEY = "social.reminder.remind.at.in.past";

    private final SocialReminderId id;
    private final OlderAdultId olderAdultId;
    private final ReminderTitle title;
    private final ReminderDescription description;
    private Instant remindAt;
    private ReminderStatus status;
    private Instant completedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    /** Reconstitution constructor — used by the persistence mapper to rebuild a reminder. */
    public SocialReminder(SocialReminderId id, OlderAdultId olderAdultId, ReminderTitle title,
                          ReminderDescription description, Instant remindAt, ReminderStatus status,
                          Instant completedAt, Instant createdAt, Instant updatedAt) {
        if (id == null || olderAdultId == null || title == null || remindAt == null || status == null
                || createdAt == null || updatedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.olderAdultId = olderAdultId;
        this.title = title;
        this.description = description;
        this.remindAt = remindAt;
        this.status = status;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Schedules the reminder, rejecting instants that already passed, and registers {@link SocialReminderScheduled}. */
    public static SocialReminder schedule(OlderAdultId olderAdultId, ReminderTitle title, ReminderDescription description,
                                          Instant remindAt, Instant scheduledAt) {
        if (remindAt == null || !remindAt.isAfter(scheduledAt))
            throw new IllegalArgumentException(REMIND_AT_IN_PAST_MESSAGE_KEY);
        var reminder = new SocialReminder(SocialReminderId.generate(), olderAdultId, title, description, remindAt,
                ReminderStatus.SCHEDULED, null, scheduledAt, scheduledAt);
        reminder.registerDomainEvent(new SocialReminderScheduled(
                reminder.id.value(), olderAdultId.value(), remindAt, scheduledAt));
        return reminder;
    }

    /**
     * Presents the reminder to the older adult when its time arrives; only for a scheduled or
     * postponed reminder. Registers {@link SocialReminderPresented}.
     */
    public void present(Instant presentedAt) {
        if (status != ReminderStatus.SCHEDULED && status != ReminderStatus.POSTPONED)
            throw new InvalidReminderStateException();
        this.status = ReminderStatus.PRESENTED;
        this.updatedAt = presentedAt;
        registerDomainEvent(new SocialReminderPresented(id.value(), olderAdultId.value(), presentedAt));
    }

    /**
     * Postpones the presented reminder by 60 minutes within the same local day; rejected if there
     * is no time left in the day. Registers {@link SocialReminderPostponed}.
     *
     * @param endOfDay the instant the older adult's current day ends
     */
    public void postpone(Instant postponedAt, Instant endOfDay) {
        if (status != ReminderStatus.PRESENTED) throw new InvalidReminderStateException();
        var newRemindAt = postponedAt.plus(POSTPONEMENT);
        if (!newRemindAt.isBefore(endOfDay)) throw new NoTimeLeftTodayException();
        this.remindAt = newRemindAt;
        this.status = ReminderStatus.POSTPONED;
        this.updatedAt = postponedAt;
        registerDomainEvent(new SocialReminderPostponed(id.value(), olderAdultId.value(), newRemindAt, postponedAt));
    }

    /** Marks the reminder as completed so it is not presented again, and registers {@link SocialReminderCompleted}. */
    public void complete(Instant completedAt) {
        if (!ACTIVE.contains(status)) throw new InvalidReminderStateException();
        this.status = ReminderStatus.COMPLETED;
        this.completedAt = completedAt;
        this.updatedAt = completedAt;
        registerDomainEvent(new SocialReminderCompleted(id.value(), olderAdultId.value(), completedAt));
    }

    /**
     * Cancels a scheduled, presented or postponed reminder so it is not presented again, and
     * registers {@link SocialReminderCanceled}.
     */
    public void cancel(Instant canceledAt) {
        if (!ACTIVE.contains(status)) throw new InvalidReminderStateException();
        this.status = ReminderStatus.CANCELED;
        this.updatedAt = canceledAt;
        registerDomainEvent(new SocialReminderCanceled(id.value(), olderAdultId.value(), canceledAt));
    }

    /**
     * Closes as missed a presented or postponed reminder whose day ended without the older adult
     * completing or canceling it. Registers {@link SocialReminderMarkedAsMissed}.
     */
    public void markAsMissed(Instant markedAt) {
        if (!isAwaitingAction()) throw new InvalidReminderStateException();
        this.status = ReminderStatus.MISSED;
        this.updatedAt = markedAt;
        registerDomainEvent(new SocialReminderMarkedAsMissed(id.value(), olderAdultId.value(), markedAt));
    }

    /** Indicates whether the reminder is presented or postponed. */
    public boolean isAwaitingAction() {
        return AWAITING_ACTION.contains(status);
    }

    /** Indicates whether the reminder belongs to the given older adult. */
    public boolean belongsTo(UUID olderAdultId) {
        return this.olderAdultId.value().equals(olderAdultId);
    }
}
