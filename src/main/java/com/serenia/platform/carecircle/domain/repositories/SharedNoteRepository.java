package com.serenia.platform.carecircle.domain.repositories;

import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.SharedNoteId;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link SharedNote} aggregate.
 */
public interface SharedNoteRepository {

    SharedNote save(SharedNote sharedNote);

    Optional<SharedNote> findById(SharedNoteId sharedNoteId);

    /** Returns the notes of the circle, from the most recent to the oldest. */
    List<SharedNote> findAllByCareCircleId(CareCircleId careCircleId);
}
