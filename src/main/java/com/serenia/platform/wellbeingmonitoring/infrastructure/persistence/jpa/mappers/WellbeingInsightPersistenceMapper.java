package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingPattern;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.CheckInId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.RelativeId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SmallWinDescription;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SmallWinId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionMessage;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingPatternId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingSuggestionId;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities.SmallWinPersistenceEntity;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities.WellbeingPatternPersistenceEntity;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities.WellbeingSuggestionPersistenceEntity;

/**
 * Stateless mapper that translates between the parts of the
 * {@link com.serenia.platform.wellbeingmonitoring.domain.model.aggregates.WellbeingInsight} aggregate
 * — patterns, suggestions and small wins — and their persistence entities. Each row carries the
 * older adult, which is the identity of the aggregate.
 */
public final class WellbeingInsightPersistenceMapper {

    private WellbeingInsightPersistenceMapper() {
    }

    public static WellbeingPattern toDomainFromPersistence(WellbeingPatternPersistenceEntity entity) {
        return new WellbeingPattern(
                new WellbeingPatternId(entity.getId()),
                entity.getType(),
                entity.getConsecutiveDays(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getDetectedAt());
    }

    public static WellbeingPatternPersistenceEntity toPersistenceFromDomain(OlderAdultId olderAdultId, WellbeingPattern pattern) {
        var entity = new WellbeingPatternPersistenceEntity();
        entity.setId(pattern.getId().value());
        entity.setOlderAdultId(olderAdultId.value());
        entity.setType(pattern.getType());
        entity.setConsecutiveDays(pattern.getConsecutiveDays());
        entity.setStartDate(pattern.getStartDate());
        entity.setEndDate(pattern.getEndDate());
        entity.setDetectedAt(pattern.getDetectedAt());
        return entity;
    }

    public static WellbeingSuggestion toDomainFromPersistence(WellbeingSuggestionPersistenceEntity entity) {
        return new WellbeingSuggestion(
                new WellbeingSuggestionId(entity.getId()),
                new WellbeingPatternId(entity.getPatternId()),
                new SuggestionMessage(entity.getMessage()),
                entity.getStatus(),
                entity.getIssuedAt(),
                entity.getDismissedBy() == null ? null : new RelativeId(entity.getDismissedBy()),
                entity.getDismissedAt());
    }

    public static WellbeingSuggestionPersistenceEntity toPersistenceFromDomain(OlderAdultId olderAdultId,
                                                                               WellbeingSuggestion suggestion) {
        var entity = new WellbeingSuggestionPersistenceEntity();
        entity.setId(suggestion.getId().value());
        entity.setOlderAdultId(olderAdultId.value());
        entity.setPatternId(suggestion.getPatternId().value());
        entity.setMessage(suggestion.getMessage().value());
        entity.setStatus(suggestion.getStatus());
        entity.setIssuedAt(suggestion.getIssuedAt());
        entity.setDismissedBy(suggestion.getDismissedBy() == null ? null : suggestion.getDismissedBy().value());
        entity.setDismissedAt(suggestion.getDismissedAt());
        return entity;
    }

    public static SmallWin toDomainFromPersistence(SmallWinPersistenceEntity entity) {
        return new SmallWin(
                new SmallWinId(entity.getId()),
                new CheckInId(entity.getCheckInId()),
                new SmallWinDescription(entity.getDescription()),
                entity.getRecordedAt());
    }

    public static SmallWinPersistenceEntity toPersistenceFromDomain(OlderAdultId olderAdultId, SmallWin smallWin) {
        var entity = new SmallWinPersistenceEntity();
        entity.setId(smallWin.getId().value());
        entity.setOlderAdultId(olderAdultId.value());
        entity.setCheckInId(smallWin.getCheckInId().value());
        entity.setDescription(smallWin.getDescription().value());
        entity.setRecordedAt(smallWin.getRecordedAt());
        return entity;
    }
}
