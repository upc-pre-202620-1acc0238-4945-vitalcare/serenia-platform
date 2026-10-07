package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.entities.QuestionPause;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInPreferencesId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.QuestionPauseId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.ReminderTime;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.TimeLimit;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.CheckInPreferencesPersistenceEntity;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.QuestionPausePersistenceEntity;

/**
 * Stateless mapper that translates between the {@link CheckInPreferences} aggregate, with its
 * latest pause, and its persistence entities.
 */
public final class CheckInPreferencesPersistenceMapper {

    private CheckInPreferencesPersistenceMapper() {
    }

    /** Reconstructs the aggregate from its row and the row of its latest pause, which may be {@code null}. */
    public static CheckInPreferences toDomainFromPersistence(CheckInPreferencesPersistenceEntity entity,
                                                             QuestionPausePersistenceEntity latestPause) {
        return new CheckInPreferences(
                new CheckInPreferencesId(entity.getId()),
                new OlderAdultId(entity.getOlderAdultId()),
                new ReminderTime(entity.getReminderTime()),
                new TimeLimit(entity.getTimeLimitMinutes()),
                entity.isSimplifiedMode(),
                latestPause == null ? null : toDomainFromPersistence(latestPause),
                entity.getUpdatedAt());
    }

    public static CheckInPreferencesPersistenceEntity toPersistenceFromDomain(CheckInPreferences preferences) {
        var entity = new CheckInPreferencesPersistenceEntity();
        entity.setId(preferences.getId().value());
        entity.setOlderAdultId(preferences.getOlderAdultId().value());
        entity.setReminderTime(preferences.getReminderTime().value());
        entity.setTimeLimitMinutes(preferences.getTimeLimit().minutes());
        entity.setSimplifiedMode(preferences.isSimplifiedMode());
        entity.setUpdatedAt(preferences.getUpdatedAt());
        return entity;
    }

    public static QuestionPause toDomainFromPersistence(QuestionPausePersistenceEntity entity) {
        return new QuestionPause(new QuestionPauseId(entity.getId()), entity.getPausedDate(), entity.getCreatedAt());
    }

    public static QuestionPausePersistenceEntity toPersistenceFromDomain(OlderAdultId olderAdultId, QuestionPause pause) {
        var entity = new QuestionPausePersistenceEntity();
        entity.setId(pause.getId().value());
        entity.setOlderAdultId(olderAdultId.value());
        entity.setPausedDate(pause.getPausedDate());
        entity.setCreatedAt(pause.getCreatedAt());
        return entity;
    }
}
