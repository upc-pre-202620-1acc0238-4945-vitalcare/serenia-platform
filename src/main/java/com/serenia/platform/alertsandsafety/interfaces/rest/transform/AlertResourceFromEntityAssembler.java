package com.serenia.platform.alertsandsafety.interfaces.rest.transform;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.Alert;
import com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert;
import com.serenia.platform.alertsandsafety.domain.model.aggregates.InactivityAlert;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.AlertResource;

/** Converts the {@link EmergencyAlert} and {@link InactivityAlert} aggregates into their common REST representation. */
public class AlertResourceFromEntityAssembler {
    public static AlertResource toResourceFromEntity(Alert alert) {
        var acknowledgement = alert.acknowledgement();
        var resolution = alert.resolution();
        var checkInId = alert instanceof InactivityAlert inactivity ? inactivity.getCheckInId().value() : null;
        var deliveryConfirmed = alert instanceof EmergencyAlert emergency && emergency.isDeliveryConfirmed();
        return new AlertResource(
                alert.getId().value(),
                alert.getOlderAdultId().value(),
                alert.getType().name(),
                alert.getStatus().name(),
                checkInId,
                alert.getTriggeredAt(),
                deliveryConfirmed,
                acknowledgement.map(attention -> attention.getRelativeId().value()).orElse(null),
                acknowledgement.map(attention -> attention.getActedAt()).orElse(null),
                resolution.map(attention -> attention.getRelativeId().value()).orElse(null),
                alert.getResolvedAt(),
                resolution.map(attention -> attention.getResolutionNote() == null ? null : attention.getResolutionNote().value())
                        .orElse(null));
    }
}
