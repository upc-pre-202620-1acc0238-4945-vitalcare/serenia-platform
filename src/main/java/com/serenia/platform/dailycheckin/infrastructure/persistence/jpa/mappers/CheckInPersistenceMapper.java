package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInWindow;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.PositiveActivity;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.CheckInPersistenceEntity;

/**
 * Stateless mapper that translates between the {@link CheckIn} aggregate and
 * {@link CheckInPersistenceEntity}.
 */
public final class CheckInPersistenceMapper {

    private CheckInPersistenceMapper() {
    }

    public static CheckIn toDomainFromPersistence(CheckInPersistenceEntity entity) {
        return new CheckIn(
                new CheckInId(entity.getId()),
                new OlderAdultId(entity.getOlderAdultId()),
                new CheckInQuestionId(entity.getQuestionId()),
                entity.getCheckDate(),
                new CheckInWindow(entity.getScheduledFor(), entity.getDeadlineAt()),
                entity.getPromptedAt(),
                entity.getMood(),
                PositiveActivity.fromNullable(entity.getPositiveActivity()),
                entity.getStatus(),
                entity.getAnsweredAt());
    }

    public static CheckInPersistenceEntity toPersistenceFromDomain(CheckIn checkIn) {
        var entity = new CheckInPersistenceEntity();
        entity.setId(checkIn.getId().value());
        entity.setOlderAdultId(checkIn.getOlderAdultId().value());
        entity.setQuestionId(checkIn.getQuestionId().value());
        entity.setCheckDate(checkIn.getCheckDate());
        entity.setScheduledFor(checkIn.getWindow().scheduledFor());
        entity.setDeadlineAt(checkIn.getWindow().deadlineAt());
        entity.setPromptedAt(checkIn.getPromptedAt());
        entity.setMood(checkIn.getMood());
        entity.setPositiveActivity(checkIn.getPositiveActivity() == null ? null : checkIn.getPositiveActivity().value());
        entity.setStatus(checkIn.getStatus());
        entity.setAnsweredAt(checkIn.getAnsweredAt());
        return entity;
    }
}
