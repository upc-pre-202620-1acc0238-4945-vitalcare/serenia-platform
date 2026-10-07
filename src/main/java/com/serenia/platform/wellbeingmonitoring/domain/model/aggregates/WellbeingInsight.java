package com.serenia.platform.wellbeingmonitoring.domain.model.aggregates;

import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.serenia.platform.wellbeingmonitoring.domain.exceptions.WellbeingPatternNotFoundException;
import com.serenia.platform.wellbeingmonitoring.domain.exceptions.WellbeingSuggestionAlreadyIssuedException;
import com.serenia.platform.wellbeingmonitoring.domain.exceptions.WellbeingSuggestionNotActiveException;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingPattern;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.domain.model.events.DiscomfortPatternDetected;
import com.serenia.platform.wellbeingmonitoring.domain.model.events.SmallWinRecorded;
import com.serenia.platform.wellbeingmonitoring.domain.model.events.WellbeingSuggestionDismissed;
import com.serenia.platform.wellbeingmonitoring.domain.model.events.WellbeingSuggestionIssued;
import com.serenia.platform.wellbeingmonitoring.domain.model.events.WellbeingTrendImproved;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.CheckInId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.MoodLevel;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.RelativeId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SmallWinDescription;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionMessage;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingPatternId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingSuggestionId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate root representing the interpretation of an older adult's wellbeing: discomfort
 * patterns, suggestions and small wins. Its identity is the older adult's.
 *
 * <p>Since the history grows every evaluated day, it only loads what its rules need: the latest
 * discomfort pattern, to decide whether a streak continues or starts, and the active
 * suggestions, so a pattern never yields more than one suggestion.</p>
 */
public class WellbeingInsight extends AbstractDomainAggregateRoot<WellbeingInsight> {

    /** Consecutive days of low mood that make a sustained discomfort. */
    public static final int DISCOMFORT_STREAK_DAYS = 3;

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "wellbeing.insight.required.value";

    private final OlderAdultId olderAdultId;
    private WellbeingPattern latestDiscomfortPattern;
    private final List<WellbeingSuggestion> activeSuggestions;
    private final List<SmallWin> newSmallWins = new ArrayList<>();

    /**
     * Reconstitution constructor — used by the repository to compose the aggregate from its
     * latest pattern and its active suggestions.
     */
    public WellbeingInsight(OlderAdultId olderAdultId, WellbeingPattern latestDiscomfortPattern,
                            List<WellbeingSuggestion> activeSuggestions) {
        if (olderAdultId == null) throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.olderAdultId = olderAdultId;
        this.latestDiscomfortPattern = latestDiscomfortPattern;
        this.activeSuggestions = activeSuggestions == null ? new ArrayList<>() : new ArrayList<>(activeSuggestions);
    }

    /** Creates the interpretation of an older adult who has nothing evaluated yet. */
    public static WellbeingInsight empty(OlderAdultId olderAdultId) {
        return new WellbeingInsight(olderAdultId, null, List.of());
    }

    /**
     * Evaluates the answered day together with the previous ones. When three consecutive days of
     * discomfort are completed, detects a pattern and registers {@link DiscomfortPatternDetected};
     * if the streak already had a pattern, extends it without detecting a new one. When the day
     * shows positive wellbeing, registers {@link WellbeingTrendImproved}.
     *
     * @param recentMoods moods of the previous days by date; days without an answer are absent
     */
    public void evaluate(CheckInId checkInId, LocalDate checkDate, MoodLevel mood, String positiveActivity,
                         Map<LocalDate, MoodLevel> recentMoods, Instant evaluatedAt) {
        if (checkInId == null || checkDate == null || mood == null || evaluatedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);

        var moods = new HashMap<>(recentMoods == null ? Map.of() : recentMoods);
        moods.put(checkDate, mood);
        var streak = discomfortStreakEndingOn(checkDate, moods);

        if (streak >= DISCOMFORT_STREAK_DAYS) {
            if (latestDiscomfortPattern != null && latestDiscomfortPattern.continuesOn(checkDate)) {
                latestDiscomfortPattern.extendTo(checkDate);
            } else if (latestDiscomfortPattern == null || !latestDiscomfortPattern.covers(checkDate)) {
                latestDiscomfortPattern = WellbeingPattern.sustainedDiscomfort(
                        checkDate.minusDays(streak - 1L), checkDate, evaluatedAt);
                registerDomainEvent(new DiscomfortPatternDetected(
                        olderAdultId.value(), latestDiscomfortPattern.getId().value(),
                        latestDiscomfortPattern.getConsecutiveDays(), latestDiscomfortPattern.getStartDate(),
                        latestDiscomfortPattern.getEndDate(), evaluatedAt));
            }
        }

        if (isPositiveDay(mood, positiveActivity)) {
            registerDomainEvent(new WellbeingTrendImproved(
                    olderAdultId.value(), checkInId.value(), checkDate,
                    positiveActivity == null || positiveActivity.isBlank() ? null : positiveActivity.trim(),
                    evaluatedAt));
        }
    }

    /**
     * Issues an action suggestion for a pattern, rejecting a second suggestion for the same
     * pattern. Registers {@link WellbeingSuggestionIssued}.
     */
    public WellbeingSuggestion issueSuggestion(WellbeingPatternId patternId, SuggestionMessage message, Instant issuedAt) {
        if (latestDiscomfortPattern == null || !latestDiscomfortPattern.getId().equals(patternId))
            throw new WellbeingPatternNotFoundException();
        if (activeSuggestions.stream().anyMatch(suggestion -> suggestion.getPatternId().equals(patternId)))
            throw new WellbeingSuggestionAlreadyIssuedException();

        var suggestion = WellbeingSuggestion.issue(patternId, message, issuedAt);
        activeSuggestions.add(suggestion);
        registerDomainEvent(new WellbeingSuggestionIssued(
                olderAdultId.value(), suggestion.getId().value(), patternId.value(), issuedAt));
        return suggestion;
    }

    /** Records a small win of a check-in and registers {@link SmallWinRecorded}. */
    public SmallWin recordSmallWin(CheckInId checkInId, SmallWinDescription description, Instant recordedAt) {
        var smallWin = SmallWin.record(checkInId, description, recordedAt);
        newSmallWins.add(smallWin);
        registerDomainEvent(new SmallWinRecorded(
                olderAdultId.value(), smallWin.getId().value(), checkInId.value(), recordedAt));
        return smallWin;
    }

    /** Dismisses an active suggestion for every relative and registers {@link WellbeingSuggestionDismissed}. */
    public WellbeingSuggestion dismissSuggestion(WellbeingSuggestionId suggestionId, RelativeId relativeId,
                                                 Instant dismissedAt) {
        var suggestion = activeSuggestions.stream()
                .filter(candidate -> candidate.getId().equals(suggestionId) && candidate.isActive())
                .findFirst()
                .orElseThrow(WellbeingSuggestionNotActiveException::new);
        suggestion.dismiss(relativeId, dismissedAt);
        registerDomainEvent(new WellbeingSuggestionDismissed(
                olderAdultId.value(), suggestionId.value(), relativeId.value(), dismissedAt));
        return suggestion;
    }

    /** Counts the consecutive days of low or very low mood ending on the date; a day without an answer breaks the streak. */
    private static int discomfortStreakEndingOn(LocalDate date, Map<LocalDate, MoodLevel> moods) {
        var streak = 0;
        var day = date;
        while (moods.containsKey(day) && moods.get(day).isDiscomfort()) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }

    /** Indicates whether the day shows positive wellbeing: good or very good mood, or a reported positive activity. */
    private static boolean isPositiveDay(MoodLevel mood, String positiveActivity) {
        return mood.isPositive() || (positiveActivity != null && !positiveActivity.isBlank());
    }

    public OlderAdultId getOlderAdultId() {
        return olderAdultId;
    }

    public WellbeingPattern getLatestDiscomfortPattern() {
        return latestDiscomfortPattern;
    }

    /** Suggestions loaded as active, including those dismissed during the current operation. */
    public List<WellbeingSuggestion> getActiveSuggestions() {
        return Collections.unmodifiableList(activeSuggestions);
    }

    public List<SmallWin> getNewSmallWins() {
        return Collections.unmodifiableList(newSmallWins);
    }
}
