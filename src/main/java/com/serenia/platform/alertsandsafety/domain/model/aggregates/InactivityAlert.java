package com.serenia.platform.alertsandsafety.domain.model.aggregates;

import com.serenia.platform.alertsandsafety.domain.exceptions.AlertAlreadyAcknowledgedException;
import com.serenia.platform.alertsandsafety.domain.exceptions.AlertNotAcknowledgedException;
import com.serenia.platform.alertsandsafety.domain.exceptions.InvalidAlertStateException;
import com.serenia.platform.alertsandsafety.domain.model.entities.AlertAttention;
import com.serenia.platform.alertsandsafety.domain.model.events.InactivityAlertAcknowledged;
import com.serenia.platform.alertsandsafety.domain.model.events.InactivityAlertRaised;
import com.serenia.platform.alertsandsafety.domain.model.events.InactivityAlertResolved;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertStatus;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.CheckInId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.ResolutionNote;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate root representing that the older adult did not answer the check-in within the
 * deadline. Bound to the check-in that originated it; each check-in raises at most one alert.
 */
public final class InactivityAlert extends AbstractDomainAggregateRoot<InactivityAlert> implements Alert {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "alert.required.value";

    private final AlertId id;
    private final OlderAdultId olderAdultId;
    private final CheckInId checkInId;
    private AlertStatus status;
    private final Instant triggeredAt;
    private Instant resolvedAt;
    private final List<AlertAttention> attentions;

    /** Reconstitution constructor — used by the persistence mapper to rebuild an alert with its attentions. */
    public InactivityAlert(AlertId id, OlderAdultId olderAdultId, CheckInId checkInId, AlertStatus status,
                           Instant triggeredAt, Instant resolvedAt, List<AlertAttention> attentions) {
        if (id == null || olderAdultId == null || checkInId == null || status == null || triggeredAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.olderAdultId = olderAdultId;
        this.checkInId = checkInId;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
        this.attentions = attentions == null ? new ArrayList<>() : new ArrayList<>(attentions);
    }

    /**
     * Decides whether a missed check-in is inactivity that must be alerted: the check-in must not
     * have an alert yet, and the older adult must have at least one linked relative to inform.
     */
    public static boolean evaluate(boolean alreadyRaisedForCheckIn, int activeRelativeCount) {
        return !alreadyRaisedForCheckIn && activeRelativeCount > 0;
    }

    /** Raises the inactivity alert and registers {@link InactivityAlertRaised}. */
    public static InactivityAlert raise(OlderAdultId olderAdultId, CheckInId checkInId, Instant raisedAt) {
        var alert = new InactivityAlert(AlertId.generate(), olderAdultId, checkInId, AlertStatus.RAISED, raisedAt, null, List.of());
        alert.registerDomainEvent(new InactivityAlertRaised(alert.id.value(), olderAdultId.value(), checkInId.value(), raisedAt));
        return alert;
    }

    /**
     * Records that a relative is attending the alert and registers {@link InactivityAlertAcknowledged}.
     * Only for an alert not yet acknowledged.
     */
    public void acknowledge(RelativeId relativeId, Instant acknowledgedAt) {
        if (status != AlertStatus.RAISED) throw new AlertAlreadyAcknowledgedException();
        attentions.add(AlertAttention.acknowledgement(relativeId, acknowledgedAt));
        this.status = AlertStatus.ACKNOWLEDGED;
        registerDomainEvent(new InactivityAlertAcknowledged(id.value(), relativeId.value(), acknowledgedAt));
    }

    /**
     * Marks as resolved an alert acknowledged before, keeping the resolution note, and registers
     * {@link InactivityAlertResolved}.
     */
    public void resolve(RelativeId relativeId, ResolutionNote resolutionNote, Instant resolvedAt) {
        if (status == AlertStatus.RESOLVED) throw new InvalidAlertStateException();
        if (status != AlertStatus.ACKNOWLEDGED) throw new AlertNotAcknowledgedException();
        attentions.add(AlertAttention.resolution(relativeId, resolutionNote, resolvedAt));
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = resolvedAt;
        registerDomainEvent(new InactivityAlertResolved(id.value(), relativeId.value(), resolvedAt));
    }

    @Override
    public AlertId getId() {
        return id;
    }

    @Override
    public AlertType getType() {
        return AlertType.INACTIVITY;
    }

    @Override
    public OlderAdultId getOlderAdultId() {
        return olderAdultId;
    }

    public CheckInId getCheckInId() {
        return checkInId;
    }

    @Override
    public AlertStatus getStatus() {
        return status;
    }

    @Override
    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    @Override
    public Instant getResolvedAt() {
        return resolvedAt;
    }

    @Override
    public List<AlertAttention> getAttentions() {
        return Collections.unmodifiableList(attentions);
    }
}
