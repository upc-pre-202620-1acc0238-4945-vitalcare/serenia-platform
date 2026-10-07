package com.serenia.platform.wellbeingmonitoring.domain.model.entities;

import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.RelativeId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionMessage;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionStatus;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingPatternId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingSuggestionId;
import lombok.Getter;

import java.time.Instant;

/**
 * Recommendation addressed to the relatives, such as calling or visiting the older adult,
 * issued from a discomfort pattern.
 *
 * <p>Belongs to the {@link com.serenia.platform.wellbeingmonitoring.domain.model.aggregates.WellbeingInsight}
 * aggregate.</p>
 */
@Getter
public class WellbeingSuggestion {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "wellbeing.suggestion.required.value";

    private final WellbeingSuggestionId id;
    private final WellbeingPatternId patternId;
    private final SuggestionMessage message;
    private SuggestionStatus status;
    private final Instant issuedAt;
    private RelativeId dismissedBy;
    private Instant dismissedAt;

    public WellbeingSuggestion(WellbeingSuggestionId id, WellbeingPatternId patternId, SuggestionMessage message,
                               SuggestionStatus status, Instant issuedAt, RelativeId dismissedBy, Instant dismissedAt) {
        if (id == null || patternId == null || message == null || status == null || issuedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.patternId = patternId;
        this.message = message;
        this.status = status;
        this.issuedAt = issuedAt;
        this.dismissedBy = dismissedBy;
        this.dismissedAt = dismissedAt;
    }

    /** Creates an active suggestion for a pattern. */
    public static WellbeingSuggestion issue(WellbeingPatternId patternId, SuggestionMessage message, Instant issuedAt) {
        return new WellbeingSuggestion(WellbeingSuggestionId.generate(), patternId, message,
                SuggestionStatus.ACTIVE, issuedAt, null, null);
    }

    /** Marks the suggestion as dismissed. */
    public void dismiss(RelativeId relativeId, Instant dismissedAt) {
        this.status = SuggestionStatus.DISMISSED;
        this.dismissedBy = relativeId;
        this.dismissedAt = dismissedAt;
    }

    /** Indicates whether the suggestion is still active. */
    public boolean isActive() {
        return status == SuggestionStatus.ACTIVE;
    }
}
