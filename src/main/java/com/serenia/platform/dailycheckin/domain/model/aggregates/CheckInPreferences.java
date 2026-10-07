package com.serenia.platform.dailycheckin.domain.model.aggregates;

import com.serenia.platform.dailycheckin.domain.exceptions.DailyCheckInNotPausedException;
import com.serenia.platform.dailycheckin.domain.exceptions.DailyPauseAlreadyActiveException;
import com.serenia.platform.dailycheckin.domain.model.entities.QuestionPause;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInPreferencesInitialized;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInScheduled;
import com.serenia.platform.dailycheckin.domain.model.events.DailyCheckInResumed;
import com.serenia.platform.dailycheckin.domain.model.events.DailyPauseActivated;
import com.serenia.platform.dailycheckin.domain.model.events.SimplifiedModeDisabled;
import com.serenia.platform.dailycheckin.domain.model.events.SimplifiedModeEnabled;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInPreferencesId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInWindow;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.ReminderTime;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.TimeLimit;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Aggregate root representing the daily check-in configuration of an older adult.
 *
 * <p>Controls the reminder time, the time limit, the simplified mode and the pause of the
 * questions, which the older adult can activate and deactivate during the day. Both the
 * configuration and the pause decide when, and whether, the question is presented.</p>
 */
@Getter
public class CheckInPreferences extends AbstractDomainAggregateRoot<CheckInPreferences> {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "check.in.preferences.required.value";

    private final CheckInPreferencesId id;
    private final OlderAdultId olderAdultId;
    private ReminderTime reminderTime;
    private final TimeLimit timeLimit;
    private boolean simplifiedMode;
    private QuestionPause latestPause;
    private Instant updatedAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild the preferences,
     * with their latest pause, from stored data.
     */
    public CheckInPreferences(CheckInPreferencesId id, OlderAdultId olderAdultId, ReminderTime reminderTime,
                              TimeLimit timeLimit, boolean simplifiedMode, QuestionPause latestPause,
                              Instant updatedAt) {
        if (id == null || olderAdultId == null || reminderTime == null || timeLimit == null || updatedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.olderAdultId = olderAdultId;
        this.reminderTime = reminderTime;
        this.timeLimit = timeLimit;
        this.simplifiedMode = simplifiedMode;
        this.latestPause = latestPause;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates the preferences with the default reminder time and time limit and registers
     * {@link CheckInPreferencesInitialized}.
     */
    public static CheckInPreferences initialize(OlderAdultId olderAdultId, Instant updatedAt) {
        var preferences = new CheckInPreferences(CheckInPreferencesId.generate(), olderAdultId,
                ReminderTime.DEFAULT, TimeLimit.DEFAULT, false, null, updatedAt);
        preferences.registerDomainEvent(new CheckInPreferencesInitialized(olderAdultId.value(), updatedAt));
        return preferences;
    }

    /**
     * Changes the reminder time; it applies from the next check-in, because the window of an
     * already opened check-in is not recomputed. Registers {@link CheckInScheduled}.
     */
    public void schedule(ReminderTime reminderTime, Instant updatedAt) {
        if (reminderTime == null) throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.reminderTime = reminderTime;
        this.updatedAt = updatedAt;
        registerDomainEvent(new CheckInScheduled(olderAdultId.value(), reminderTime.value(), updatedAt));
    }

    /** Pauses the questions of the current day and registers {@link DailyPauseActivated}. */
    public void activateDailyPause(LocalDate today, Instant createdAt) {
        if (isPausedOn(today)) throw new DailyPauseAlreadyActiveException();
        this.latestPause = QuestionPause.of(today, createdAt);
        this.updatedAt = createdAt;
        registerDomainEvent(new DailyPauseActivated(olderAdultId.value(), today, createdAt));
    }

    /** Resumes the questions of the current day and registers {@link DailyCheckInResumed}. */
    public void resumeToday(LocalDate today, Instant resumedAt) {
        if (!isPausedOn(today)) throw new DailyCheckInNotPausedException();
        this.latestPause = null;
        this.updatedAt = resumedAt;
        registerDomainEvent(new DailyCheckInResumed(olderAdultId.value(), today, resumedAt));
    }

    /**
     * Registers {@link DailyCheckInResumed} if the previous day ended with the questions paused;
     * otherwise makes no changes.
     *
     * @return {@code true} if the resumption was registered
     */
    public boolean resumeAfterPausedDay(LocalDate today) {
        if (!isPausedOn(today.minusDays(1))) return false;
        registerDomainEvent(new DailyCheckInResumed(olderAdultId.value(), today, Instant.now()));
        return true;
    }

    /** Enables the simplified mode and registers {@link SimplifiedModeEnabled}, unless it was already enabled. */
    public void enableSimplifiedMode(Instant updatedAt) {
        if (simplifiedMode) return;
        this.simplifiedMode = true;
        this.updatedAt = updatedAt;
        registerDomainEvent(new SimplifiedModeEnabled(olderAdultId.value(), updatedAt));
    }

    /** Disables the simplified mode and registers {@link SimplifiedModeDisabled}, unless it was already disabled. */
    public void disableSimplifiedMode(Instant updatedAt) {
        if (!simplifiedMode) return;
        this.simplifiedMode = false;
        this.updatedAt = updatedAt;
        registerDomainEvent(new SimplifiedModeDisabled(olderAdultId.value(), updatedAt));
    }

    /** Indicates whether the questions are paused on the given date. */
    public boolean isPausedOn(LocalDate date) {
        return latestPause != null && latestPause.appliesTo(date);
    }

    /**
     * Computes the presentation and deadline instants of the check-in of a date, from the
     * reminder time, the time limit and the older adult's time zone.
     */
    public CheckInWindow windowFor(LocalDate checkDate, ZoneId timeZone) {
        var scheduledFor = checkDate.atTime(reminderTime.value()).atZone(timeZone).toInstant();
        return new CheckInWindow(scheduledFor, scheduledFor.plus(timeLimit.toDuration()));
    }
}
