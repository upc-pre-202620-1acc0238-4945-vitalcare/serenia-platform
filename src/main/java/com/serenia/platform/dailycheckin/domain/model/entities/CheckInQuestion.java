package com.serenia.platform.dailycheckin.domain.model.entities;

import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.QuestionText;
import lombok.Getter;

/**
 * Question of the catalog that can be presented in a check-in.
 *
 * <p>Read-only catalog entity: it is loaded when the application starts, is not part of any
 * aggregate and is read through its own repository.</p>
 */
@Getter
public class CheckInQuestion {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "check.in.question.required.value";

    private final CheckInQuestionId id;
    private final QuestionText text;
    private final String tone;
    private final boolean active;

    public CheckInQuestion(CheckInQuestionId id, QuestionText text, String tone, boolean active) {
        if (id == null || text == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.text = text;
        this.tone = tone;
        this.active = active;
    }

    /** Creates an active question of the catalog. */
    public static CheckInQuestion of(String text, String tone) {
        return new CheckInQuestion(CheckInQuestionId.generate(), new QuestionText(text), tone, true);
    }

    /** Indicates whether the question can be selected for a check-in. */
    public boolean isActive() {
        return active;
    }
}
