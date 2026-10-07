package com.serenia.platform.dailycheckin.domain.model.entities;

import com.serenia.platform.dailycheckin.domain.model.valueobjects.QuestionPauseId;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Decision of the older adult not to receive questions on a date.
 *
 * <p>Belongs to the {@link com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences}
 * aggregate, which only loads the latest pause; previous ones stay in the database as history.</p>
 */
@Getter
public class QuestionPause {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "question.pause.required.value";

    private final QuestionPauseId id;
    private final LocalDate pausedDate;
    private final Instant createdAt;

    public QuestionPause(QuestionPauseId id, LocalDate pausedDate, Instant createdAt) {
        if (id == null || pausedDate == null || createdAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.pausedDate = pausedDate;
        this.createdAt = createdAt;
    }

    /** Creates the pause of a date. */
    public static QuestionPause of(LocalDate pausedDate, Instant createdAt) {
        return new QuestionPause(QuestionPauseId.generate(), pausedDate, createdAt);
    }

    /** Indicates whether the pause corresponds to the given date. */
    public boolean appliesTo(LocalDate date) {
        return pausedDate.equals(date);
    }
}
