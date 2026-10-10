package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.dailycheckin.domain.model.entities.CheckInQuestion;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.QuestionText;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.CheckInQuestionPersistenceEntity;

/**
 * Stateless mapper that translates between the {@link CheckInQuestion} entity and
 * {@link CheckInQuestionPersistenceEntity}.
 */
public final class CheckInQuestionPersistenceMapper {

    private CheckInQuestionPersistenceMapper() {
    }

    public static CheckInQuestion toDomainFromPersistence(CheckInQuestionPersistenceEntity entity) {
        return new CheckInQuestion(
                new CheckInQuestionId(entity.getId()),
                new QuestionText(entity.getText()),
                entity.getTone(),
                entity.isActive());
    }

    public static CheckInQuestionPersistenceEntity toPersistenceFromDomain(CheckInQuestion question) {
        var entity = new CheckInQuestionPersistenceEntity();
        entity.setId(question.getId().value());
        entity.setText(question.getText().value());
        entity.setTone(question.getTone());
        entity.setActive(question.isActive());
        return entity;
    }
}
