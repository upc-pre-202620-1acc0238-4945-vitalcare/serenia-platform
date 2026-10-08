package com.serenia.platform.alertsandsafety.domain.model.aggregates;

import com.serenia.platform.alertsandsafety.domain.model.entities.AlertAttention;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertStatus;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AttentionAction;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * What both alert aggregates share: identity, status and the attention cycle of the relatives.
 *
 * <p>Lets the alerts view combine emergencies and inactivity alerts in a single history.</p>
 */
public sealed interface Alert permits EmergencyAlert, InactivityAlert {

    AlertId getId();

    AlertType getType();

    OlderAdultId getOlderAdultId();

    AlertStatus getStatus();

    Instant getTriggeredAt();

    Instant getResolvedAt();

    List<AlertAttention> getAttentions();

    /** Indicates whether the alert has not been resolved yet. */
    default boolean isOpen() {
        return getStatus() != AlertStatus.RESOLVED;
    }

    /** Returns the acknowledgement of a relative, if any. */
    default Optional<AlertAttention> acknowledgement() {
        return attentionOf(AttentionAction.ACKNOWLEDGED);
    }

    /** Returns the resolution of a relative, if any. */
    default Optional<AlertAttention> resolution() {
        return attentionOf(AttentionAction.RESOLVED);
    }

    private Optional<AlertAttention> attentionOf(AttentionAction action) {
        return getAttentions().stream().filter(attention -> attention.getAction() == action).findFirst();
    }
}
