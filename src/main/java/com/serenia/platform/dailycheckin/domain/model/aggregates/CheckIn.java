package com.serenia.platform.dailycheckin.domain.model.aggregates;

import com.serenia.platform.dailycheckin.domain.exceptions.CheckInAlreadyPromptedException;
import com.serenia.platform.dailycheckin.domain.exceptions.CheckInDeadlineNotReachedException;
import com.serenia.platform.dailycheckin.domain.exceptions.CheckInDeadlinePassedException;
import com.serenia.platform.dailycheckin.domain.exceptions.CheckInNotPendingException;
import com.serenia.platform.dailycheckin.domain.exceptions.InvalidDeadlineExtensionException;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInAnswered;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInMissed;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInOpened;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInPrompted;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInSkipped;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInStatus;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInWindow;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.MoodLevel;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.PositiveActivity;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Aggregate root representing the interaction of one day with the older adult.
 *
 * <p>Controls the transitions between pending, answered, missed and skipped. There is a single
 * check-in per older adult and date.</p>
 */
@Getter
public class CheckIn extends AbstractDomainAggregateRoot<CheckIn> {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "check.in.required.value";
    private static final String MOOD_REQUIRED_MESSAGE_KEY = "check.in.mood.required";

    private final CheckInId id;
    private final OlderAdultId olderAdultId;
    private final CheckInQuestionId questionId;
    private final LocalDate checkDate;
    private CheckInWindow window;
    private Instant promptedAt;
    private MoodLevel mood;
    private PositiveActivity positiveActivity;
    private CheckInStatus status;
    private Instant answeredAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild a check-in
     * from stored data.
     */
    public CheckIn(CheckInId id, OlderAdultId olderAdultId, CheckInQuestionId questionId, LocalDate checkDate,
                   CheckInWindow window, Instant promptedAt, MoodLevel mood, PositiveActivity positiveActivity,
                   CheckInStatus status, Instant answeredAt) {
        if (id == null || olderAdultId == null || questionId == null || checkDate == null
                || window == null || status == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.olderAdultId = olderAdultId;
        this.questionId = questionId;
        this.checkDate = checkDate;
        this.window = window;
        this.promptedAt = promptedAt;
        this.mood = mood;
        this.positiveActivity = positiveActivity;
        this.status = status;
        this.answeredAt = answeredAt;
    }

    /** Opens the pending check-in of a date and registers {@link CheckInOpened}. */
    public static CheckIn open(OlderAdultId olderAdultId, CheckInQuestionId questionId, LocalDate checkDate,
                               CheckInWindow window) {
        var checkIn = new CheckIn(CheckInId.generate(), olderAdultId, questionId, checkDate, window,
                null, null, null, CheckInStatus.PENDING, null);
        checkIn.registerDomainEvent(new CheckInOpened(
                checkIn.id.value(), olderAdultId.value(), checkDate, Instant.now()));
        return checkIn;
    }

    /**
     * Records that the older adult was told the check-in is available; only for a pending
     * check-in not prompted yet. Registers {@link CheckInPrompted}.
     */
    public void prompt(Instant promptedAt) {
        ensurePending();
        if (this.promptedAt != null) throw new CheckInAlreadyPromptedException();
        this.promptedAt = promptedAt;
        registerDomainEvent(new CheckInPrompted(id.value(), olderAdultId.value(), promptedAt));
    }

    /**
     * Records the answer; only for a pending check-in whose deadline has not passed. It can be
     * answered before the reminder time. Registers {@link CheckInAnswered}.
     */
    public void answer(MoodLevel mood, PositiveActivity positiveActivity, Instant answeredAt) {
        if (mood == null) throw new IllegalArgumentException(MOOD_REQUIRED_MESSAGE_KEY);
        ensurePending();
        if (!answeredAt.isBefore(window.deadlineAt())) throw new CheckInDeadlinePassedException();
        this.mood = mood;
        this.positiveActivity = positiveActivity;
        this.answeredAt = answeredAt;
        this.status = CheckInStatus.ANSWERED;
        registerDomainEvent(new CheckInAnswered(
                id.value(), olderAdultId.value(), checkDate, mood.name(),
                positiveActivity == null ? null : positiveActivity.value(), answeredAt));
    }

    /** Gives a pending check-in a new deadline, which must be after the current one. */
    public void extendDeadline(Instant newDeadlineAt) {
        ensurePending();
        if (newDeadlineAt == null || !newDeadlineAt.isAfter(window.deadlineAt()))
            throw new InvalidDeadlineExtensionException();
        this.window = window.withDeadline(newDeadlineAt);
    }

    /** Marks as missed a pending check-in whose deadline has passed and registers {@link CheckInMissed}. */
    public void expire(Instant referenceTime) {
        ensurePending();
        if (referenceTime.isBefore(window.deadlineAt())) throw new CheckInDeadlineNotReachedException();
        this.status = CheckInStatus.MISSED;
        registerDomainEvent(new CheckInMissed(id.value(), olderAdultId.value(), checkDate, referenceTime));
    }

    /**
     * Marks as skipped a pending check-in whose day ended with the questions paused and
     * registers {@link CheckInSkipped}.
     */
    public void skip(Instant skippedAt) {
        ensurePending();
        this.status = CheckInStatus.SKIPPED;
        registerDomainEvent(new CheckInSkipped(id.value(), olderAdultId.value(), checkDate, skippedAt));
    }

    /** Indicates whether the check-in is still pending. */
    public boolean isPending() {
        return status == CheckInStatus.PENDING;
    }

    private void ensurePending() {
        if (!isPending()) throw new CheckInNotPendingException();
    }
}
