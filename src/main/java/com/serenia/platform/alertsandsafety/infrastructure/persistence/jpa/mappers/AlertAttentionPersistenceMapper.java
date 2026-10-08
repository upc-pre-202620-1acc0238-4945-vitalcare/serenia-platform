package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.alertsandsafety.domain.model.entities.AlertAttention;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertAttentionId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.ResolutionNote;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities.AlertAttentionPersistenceEntity;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Translation of the attention actions, shared by the mappers of both alert aggregates.
 */
final class AlertAttentionPersistenceMapper {

    private AlertAttentionPersistenceMapper() {
    }

    static List<AlertAttention> toDomainFromPersistence(List<AlertAttentionPersistenceEntity> entities) {
        return entities.stream()
                .map(entity -> new AlertAttention(
                        new AlertAttentionId(entity.getId()),
                        new RelativeId(entity.getRelativeId()),
                        entity.getAction(),
                        entity.getActedAt(),
                        ResolutionNote.fromNullable(entity.getResolutionNote())))
                .toList();
    }

    /**
     * Adds the actions the row does not hold yet. Actions are never modified nor removed, so a
     * concurrent action of another relative collides with the unique index instead of being lost.
     */
    static void addNewAttentions(List<AlertAttention> attentions, AlertPersistenceEntity entity) {
        var storedIds = entity.getAttentions().stream()
                .map(AlertAttentionPersistenceEntity::getId)
                .collect(Collectors.toSet());
        attentions.stream()
                .filter(attention -> !storedIds.contains(attention.getId().value()))
                .forEach(attention -> {
                    var row = new AlertAttentionPersistenceEntity();
                    row.setId(attention.getId().value());
                    row.setRelativeId(attention.getRelativeId().value());
                    row.setAction(attention.getAction());
                    row.setActedAt(attention.getActedAt());
                    row.setResolutionNote(attention.getResolutionNote() == null ? null : attention.getResolutionNote().value());
                    entity.getAttentions().add(row);
                });
    }
}
