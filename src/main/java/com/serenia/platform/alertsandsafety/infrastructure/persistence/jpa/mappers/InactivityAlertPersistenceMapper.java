package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.InactivityAlert;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.CheckInId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;

/**
 * Stateless mapper that translates between the {@link InactivityAlert} aggregate, with its
 * attention actions, and the rows of type INACTIVITY of the alerts.
 */
public final class InactivityAlertPersistenceMapper {

    private InactivityAlertPersistenceMapper() {
    }

    public static InactivityAlert toDomainFromPersistence(AlertPersistenceEntity entity) {
        return new InactivityAlert(
                new AlertId(entity.getId()),
                new OlderAdultId(entity.getOlderAdultId()),
                new CheckInId(entity.getCheckInId()),
                entity.getStatus(),
                entity.getTriggeredAt(),
                entity.getResolvedAt(),
                AlertAttentionPersistenceMapper.toDomainFromPersistence(entity.getAttentions()));
    }

    /** Creates the row of a new alert, without children; they are added by {@link #applyToPersistence}. */
    public static AlertPersistenceEntity newPersistenceFromDomain(InactivityAlert alert) {
        var entity = new AlertPersistenceEntity();
        entity.setId(alert.getId().value());
        entity.setType(AlertType.INACTIVITY);
        entity.setOlderAdultId(alert.getOlderAdultId().value());
        entity.setCheckInId(alert.getCheckInId().value());
        entity.setTriggeredAt(alert.getTriggeredAt());
        return entity;
    }

    /** Copies the state of the aggregate onto its row, adding the new attention actions. */
    public static void applyToPersistence(InactivityAlert alert, AlertPersistenceEntity entity) {
        entity.setStatus(alert.getStatus());
        entity.setResolvedAt(alert.getResolvedAt());
        AlertAttentionPersistenceMapper.addNewAttentions(alert.getAttentions(), entity);
    }
}
