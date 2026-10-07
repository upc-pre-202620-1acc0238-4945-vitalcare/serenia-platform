package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderDescription;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderTitle;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.SocialReminderId;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities.SocialReminderPersistenceEntity;

/**
 * Stateless mapper that translates between the {@link SocialReminder} aggregate and
 * {@link SocialReminderPersistenceEntity}.
 */
public final class SocialReminderPersistenceMapper {

    private SocialReminderPersistenceMapper() {
    }

    public static SocialReminder toDomainFromPersistence(SocialReminderPersistenceEntity entity) {
        return new SocialReminder(
                new SocialReminderId(entity.getId()),
                new OlderAdultId(entity.getOlderAdultId()),
                new ReminderTitle(entity.getTitle()),
                ReminderDescription.fromNullable(entity.getDescription()),
                entity.getRemindAt(),
                entity.getStatus(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static SocialReminderPersistenceEntity toPersistenceFromDomain(SocialReminder reminder) {
        var entity = new SocialReminderPersistenceEntity();
        entity.setId(reminder.getId().value());
        entity.setOlderAdultId(reminder.getOlderAdultId().value());
        entity.setTitle(reminder.getTitle().value());
        entity.setDescription(reminder.getDescription() == null ? null : reminder.getDescription().value());
        entity.setRemindAt(reminder.getRemindAt());
        entity.setStatus(reminder.getStatus());
        entity.setCompletedAt(reminder.getCompletedAt());
        entity.setCreatedAt(reminder.getCreatedAt());
        entity.setUpdatedAt(reminder.getUpdatedAt());
        return entity;
    }
}
