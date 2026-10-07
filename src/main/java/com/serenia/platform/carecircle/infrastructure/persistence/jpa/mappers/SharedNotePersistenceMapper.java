package com.serenia.platform.carecircle.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.NoteContent;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.model.valueobjects.SharedNoteId;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.SharedNotePersistenceEntity;

/**
 * Stateless mapper that translates between the {@link SharedNote} aggregate and
 * {@link SharedNotePersistenceEntity}.
 */
public final class SharedNotePersistenceMapper {

    private SharedNotePersistenceMapper() {
    }

    public static SharedNote toDomainFromPersistence(SharedNotePersistenceEntity entity) {
        return new SharedNote(
                new SharedNoteId(entity.getId()),
                new CareCircleId(entity.getCareCircleId()),
                new RelativeId(entity.getAuthorId()),
                new NoteContent(entity.getContent()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static SharedNotePersistenceEntity toPersistenceFromDomain(SharedNote sharedNote) {
        var entity = new SharedNotePersistenceEntity();
        entity.setId(sharedNote.getId().value());
        entity.setCareCircleId(sharedNote.getCareCircleId().value());
        entity.setAuthorId(sharedNote.getAuthorId().value());
        entity.setContent(sharedNote.getContent().value());
        entity.setCreatedAt(sharedNote.getCreatedAt());
        entity.setUpdatedAt(sharedNote.getUpdatedAt());
        return entity;
    }
}
