package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.interfaces.rest.resources.SharedNoteResource;

/** Converts a {@link SharedNote} aggregate into its {@link SharedNoteResource} REST representation. */
public class SharedNoteResourceFromEntityAssembler {
    public static SharedNoteResource toResourceFromEntity(SharedNote sharedNote) {
        return new SharedNoteResource(
                sharedNote.getId().value(),
                sharedNote.getCareCircleId().value(),
                sharedNote.getAuthorId().value(),
                sharedNote.getContent().value(),
                sharedNote.getCreatedAt(),
                sharedNote.getUpdatedAt());
    }
}
