package com.serenia.platform.carecircle.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareShiftId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.CareShiftPersistenceEntity;

/**
 * Stateless mapper that translates between the {@link CareShift} aggregate and
 * {@link CareShiftPersistenceEntity}.
 */
public final class CareShiftPersistenceMapper {

    private CareShiftPersistenceMapper() {
    }

    public static CareShift toDomainFromPersistence(CareShiftPersistenceEntity entity) {
        return new CareShift(
                new CareShiftId(entity.getId()),
                new CareCircleId(entity.getCareCircleId()),
                new RelativeId(entity.getRelativeId()),
                entity.getShiftDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static CareShiftPersistenceEntity toPersistenceFromDomain(CareShift careShift) {
        var entity = new CareShiftPersistenceEntity();
        entity.setId(careShift.getId().value());
        entity.setCareCircleId(careShift.getCareCircleId().value());
        entity.setRelativeId(careShift.getRelativeId().value());
        entity.setShiftDate(careShift.getShiftDate());
        entity.setCreatedAt(careShift.getCreatedAt());
        entity.setUpdatedAt(careShift.getUpdatedAt());
        return entity;
    }
}
