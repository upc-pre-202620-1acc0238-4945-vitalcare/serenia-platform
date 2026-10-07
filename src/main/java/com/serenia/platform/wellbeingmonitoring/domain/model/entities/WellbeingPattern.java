package com.serenia.platform.wellbeingmonitoring.domain.model.entities;

import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.PatternType;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingPatternId;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Sustained period of discomfort of the older adult.
 *
 * <p>Belongs to the {@link com.serenia.platform.wellbeingmonitoring.domain.model.aggregates.WellbeingInsight}
 * aggregate.</p>
 */
@Getter
public class WellbeingPattern {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "wellbeing.pattern.required.value";
    private static final String INVALID_RANGE_MESSAGE_KEY = "wellbeing.pattern.range.invalid";

    private final WellbeingPatternId id;
    private final PatternType type;
    private int consecutiveDays;
    private final LocalDate startDate;
    private LocalDate endDate;
    private final Instant detectedAt;

    public WellbeingPattern(WellbeingPatternId id, PatternType type, int consecutiveDays,
                            LocalDate startDate, LocalDate endDate, Instant detectedAt) {
        if (id == null || type == null || startDate == null || endDate == null || detectedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        if (endDate.isBefore(startDate) || consecutiveDays != daysBetween(startDate, endDate))
            throw new IllegalArgumentException(INVALID_RANGE_MESSAGE_KEY);
        this.id = id;
        this.type = type;
        this.consecutiveDays = consecutiveDays;
        this.startDate = startDate;
        this.endDate = endDate;
        this.detectedAt = detectedAt;
    }

    /** Creates a sustained-discomfort pattern covering both dates, inclusive. */
    public static WellbeingPattern sustainedDiscomfort(LocalDate startDate, LocalDate endDate, Instant detectedAt) {
        return new WellbeingPattern(WellbeingPatternId.generate(), PatternType.SUSTAINED_DISCOMFORT,
                daysBetween(startDate, endDate), startDate, endDate, detectedAt);
    }

    /** Extends the pattern when the streak continues the next day, updating the number of days. */
    public void extendTo(LocalDate endDate) {
        if (!continuesOn(endDate)) throw new IllegalArgumentException(INVALID_RANGE_MESSAGE_KEY);
        this.endDate = endDate;
        this.consecutiveDays = daysBetween(startDate, endDate);
    }

    /** Indicates whether the given date is the day after the last day of the pattern. */
    public boolean continuesOn(LocalDate date) {
        return endDate.plusDays(1).equals(date);
    }

    /** Indicates whether the given date is already part of the pattern. */
    public boolean covers(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private static int daysBetween(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
