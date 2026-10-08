package com.serenia.platform.alertsandsafety.domain.model.entities;

import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertNotificationId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.DeliveryStatus;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import lombok.Getter;

import java.time.Instant;

/**
 * Notice of an emergency addressed to a linked relative; there is one per relative and alert.
 *
 * <p>Belongs to the {@link com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert} aggregate.</p>
 */
@Getter
public class AlertNotification {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "alert.notification.required.value";

    private final AlertNotificationId id;
    private final RelativeId relativeId;
    private DeliveryStatus status;
    private final Instant sentAt;
    private Instant deliveredAt;

    public AlertNotification(AlertNotificationId id, RelativeId relativeId, DeliveryStatus status,
                             Instant sentAt, Instant deliveredAt) {
        if (id == null || relativeId == null || status == null || sentAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.relativeId = relativeId;
        this.status = status;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
    }

    /** Creates the notification of a relative, sent and not yet delivered. */
    public static AlertNotification sentTo(RelativeId relativeId, Instant sentAt) {
        return new AlertNotification(AlertNotificationId.generate(), relativeId, DeliveryStatus.SENT, sentAt, null);
    }

    /** Marks the notification as delivered. */
    public void markAsDelivered(Instant deliveredAt) {
        if (isDelivered()) return;
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = deliveredAt;
    }

    public boolean isDelivered() {
        return status == DeliveryStatus.DELIVERED;
    }
}
