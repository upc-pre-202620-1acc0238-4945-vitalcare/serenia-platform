package com.serenia.platform.alertsandsafety.domain.model.aggregates;

import com.serenia.platform.alertsandsafety.domain.exceptions.AlertAlreadyAcknowledgedException;
import com.serenia.platform.alertsandsafety.domain.exceptions.AlertNotAcknowledgedException;
import com.serenia.platform.alertsandsafety.domain.exceptions.InvalidAlertStateException;
import com.serenia.platform.alertsandsafety.domain.model.entities.AlertAttention;
import com.serenia.platform.alertsandsafety.domain.model.entities.AlertNotification;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertAcknowledged;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertDeliveryConfirmed;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertDispatchFailed;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertDispatched;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertRaised;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertResolved;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertStatus;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.ResolutionNote;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate root representing a request of immediate help from the older adult.
 *
 * <p>Controls its dispatch to the linked relatives, the delivery confirmation and its attention.</p>
 */
public final class EmergencyAlert extends AbstractDomainAggregateRoot<EmergencyAlert> implements Alert {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "alert.required.value";

    private final AlertId id;
    private final OlderAdultId olderAdultId;
    private AlertStatus status;
    private final Instant triggeredAt;
    private Instant resolvedAt;
    private final List<AlertNotification> notifications;
    private final List<AlertAttention> attentions;

    /** Reconstitution constructor — used by the persistence mapper to rebuild an alert with its children. */
    public EmergencyAlert(AlertId id, OlderAdultId olderAdultId, AlertStatus status, Instant triggeredAt,
                          Instant resolvedAt, List<AlertNotification> notifications, List<AlertAttention> attentions) {
        if (id == null || olderAdultId == null || status == null || triggeredAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.olderAdultId = olderAdultId;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
        this.notifications = notifications == null ? new ArrayList<>() : new ArrayList<>(notifications);
        this.attentions = attentions == null ? new ArrayList<>() : new ArrayList<>(attentions);
    }

    /** Records that the older adult pressed the help button and registers {@link EmergencyAlertRaised}. */
    public static EmergencyAlert trigger(OlderAdultId olderAdultId, Instant triggeredAt) {
        var alert = new EmergencyAlert(AlertId.generate(), olderAdultId, AlertStatus.RAISED, triggeredAt, null,
                List.of(), List.of());
        alert.registerDomainEvent(new EmergencyAlertRaised(alert.id.value(), olderAdultId.value(), triggeredAt));
        return alert;
    }

    /**
     * Records one notification for each linked relative and registers {@link EmergencyAlertDispatched}.
     * Without linked relatives, marks the dispatch as failed and registers {@link EmergencyAlertDispatchFailed}.
     */
    public void dispatch(List<RelativeId> relativeIds, Instant dispatchedAt) {
        if (status != AlertStatus.RAISED) throw new InvalidAlertStateException();
        if (relativeIds == null || relativeIds.isEmpty()) {
            this.status = AlertStatus.DISPATCH_FAILED;
            registerDomainEvent(new EmergencyAlertDispatchFailed(id.value(), olderAdultId.value(), dispatchedAt));
            return;
        }
        relativeIds.stream().distinct().forEach(relativeId -> notifications.add(AlertNotification.sentTo(relativeId, dispatchedAt)));
        this.status = AlertStatus.DISPATCHED;
        registerDomainEvent(new EmergencyAlertDispatched(id.value(), olderAdultId.value(),
                notifications.stream().map(notification -> notification.getRelativeId().value()).toList(), dispatchedAt));
    }

    /**
     * Marks the notifications as delivered, so the alert is visible to every relative, and
     * registers {@link EmergencyAlertDeliveryConfirmed}. Only for a dispatched alert.
     */
    public void confirmDelivery(Instant confirmedAt) {
        if (status != AlertStatus.DISPATCHED) throw new InvalidAlertStateException();
        notifications.forEach(notification -> notification.markAsDelivered(confirmedAt));
        registerDomainEvent(new EmergencyAlertDeliveryConfirmed(id.value(), olderAdultId.value(), confirmedAt));
    }

    /**
     * Records that a relative is attending the emergency and registers {@link EmergencyAlertAcknowledged}.
     * Only for a dispatched alert not yet acknowledged.
     */
    public void acknowledge(RelativeId relativeId, Instant acknowledgedAt) {
        if (status == AlertStatus.ACKNOWLEDGED || status == AlertStatus.RESOLVED) throw new AlertAlreadyAcknowledgedException();
        if (status != AlertStatus.DISPATCHED) throw new InvalidAlertStateException();
        attentions.add(AlertAttention.acknowledgement(relativeId, acknowledgedAt));
        this.status = AlertStatus.ACKNOWLEDGED;
        registerDomainEvent(new EmergencyAlertAcknowledged(id.value(), relativeId.value(), acknowledgedAt));
    }

    /**
     * Marks as resolved an emergency acknowledged before, keeping the resolution note, and
     * registers {@link EmergencyAlertResolved}.
     */
    public void resolve(RelativeId relativeId, ResolutionNote resolutionNote, Instant resolvedAt) {
        if (status == AlertStatus.RESOLVED) throw new InvalidAlertStateException();
        if (status != AlertStatus.ACKNOWLEDGED) throw new AlertNotAcknowledgedException();
        attentions.add(AlertAttention.resolution(relativeId, resolutionNote, resolvedAt));
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = resolvedAt;
        registerDomainEvent(new EmergencyAlertResolved(id.value(), relativeId.value(), resolvedAt));
    }

    /** Indicates whether the delivery was confirmed, which tells the older adult that the family was notified. */
    public boolean isDeliveryConfirmed() {
        return !notifications.isEmpty() && notifications.stream().allMatch(AlertNotification::isDelivered);
    }

    @Override
    public AlertId getId() {
        return id;
    }

    @Override
    public AlertType getType() {
        return AlertType.EMERGENCY;
    }

    @Override
    public OlderAdultId getOlderAdultId() {
        return olderAdultId;
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

    public List<AlertNotification> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    @Override
    public List<AlertAttention> getAttentions() {
        return Collections.unmodifiableList(attentions);
    }
}
