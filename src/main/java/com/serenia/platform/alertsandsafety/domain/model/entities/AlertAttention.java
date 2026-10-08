package com.serenia.platform.alertsandsafety.domain.model.entities;

import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertAttentionId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AttentionAction;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.ResolutionNote;
import lombok.Getter;

import java.time.Instant;

/**
 * Action of a relative on an alert: its acknowledgement or its resolution.
 *
 * <p>Belongs to the {@link com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert}
 * and {@link com.serenia.platform.alertsandsafety.domain.model.aggregates.InactivityAlert} aggregates.</p>
 */
@Getter
public class AlertAttention {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "alert.attention.required.value";

    private final AlertAttentionId id;
    private final RelativeId relativeId;
    private final AttentionAction action;
    private final Instant actedAt;
    private final ResolutionNote resolutionNote;

    public AlertAttention(AlertAttentionId id, RelativeId relativeId, AttentionAction action, Instant actedAt,
                          ResolutionNote resolutionNote) {
        if (id == null || relativeId == null || action == null || actedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.relativeId = relativeId;
        this.action = action;
        this.actedAt = actedAt;
        // Only the resolution carries a note
        this.resolutionNote = action == AttentionAction.RESOLVED ? resolutionNote : null;
    }

    public static AlertAttention acknowledgement(RelativeId relativeId, Instant acknowledgedAt) {
        return new AlertAttention(AlertAttentionId.generate(), relativeId, AttentionAction.ACKNOWLEDGED, acknowledgedAt, null);
    }

    public static AlertAttention resolution(RelativeId relativeId, ResolutionNote resolutionNote, Instant resolvedAt) {
        return new AlertAttention(AlertAttentionId.generate(), relativeId, AttentionAction.RESOLVED, resolvedAt, resolutionNote);
    }
}
