package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert;
import com.serenia.platform.alertsandsafety.domain.model.entities.AlertNotification;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertNotificationId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities.AlertNotificationPersistenceEntity;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stateless mapper that translates between the {@link EmergencyAlert} aggregate, with its
 * notifications and attention actions, and the rows of type EMERGENCY of the alerts.
 */
public final class EmergencyAlertPersistenceMapper {

    private EmergencyAlertPersistenceMapper() {
    }

    public static EmergencyAlert toDomainFromPersistence(AlertPersistenceEntity entity) {
        return new EmergencyAlert(
                new AlertId(entity.getId()),
                new OlderAdultId(entity.getOlderAdultId()),
                entity.getStatus(),
                entity.getTriggeredAt(),
                entity.getResolvedAt(),
                entity.getNotifications().stream()
                        .map(row -> new AlertNotification(new AlertNotificationId(row.getId()), new RelativeId(row.getRelativeId()),
                                row.getStatus(), row.getSentAt(), row.getDeliveredAt()))
                        .toList(),
                AlertAttentionPersistenceMapper.toDomainFromPersistence(entity.getAttentions()));
    }

    /** Creates the row of a new alert, without children; they are added by {@link #applyToPersistence}. */
    public static AlertPersistenceEntity newPersistenceFromDomain(EmergencyAlert alert) {
        var entity = new AlertPersistenceEntity();
        entity.setId(alert.getId().value());
        entity.setType(AlertType.EMERGENCY);
        entity.setOlderAdultId(alert.getOlderAdultId().value());
        entity.setTriggeredAt(alert.getTriggeredAt());
        return entity;
    }

    /** Copies the state of the aggregate onto its row, updating notifications and adding new attention actions. */
    public static void applyToPersistence(EmergencyAlert alert, AlertPersistenceEntity entity) {
        entity.setStatus(alert.getStatus());
        entity.setResolvedAt(alert.getResolvedAt());

        var storedNotifications = entity.getNotifications().stream()
                .collect(Collectors.toMap(AlertNotificationPersistenceEntity::getId, Function.identity()));
        alert.getNotifications().forEach(notification -> {
            var row = storedNotifications.get(notification.getId().value());
            if (row == null) {
                row = new AlertNotificationPersistenceEntity();
                row.setId(notification.getId().value());
                row.setRelativeId(notification.getRelativeId().value());
                row.setSentAt(notification.getSentAt());
                entity.getNotifications().add(row);
            }
            row.setStatus(notification.getStatus());
            row.setDeliveredAt(notification.getDeliveredAt());
        });

        AlertAttentionPersistenceMapper.addNewAttentions(alert.getAttentions(), entity);
    }
}
