package com.serenia.platform.socialcompanionship.domain.repositories;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.PhotoMessageId;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link PhotoMessage} aggregate together with its receipt.
 */
public interface PhotoMessageRepository {

    PhotoMessage save(PhotoMessage photoMessage);

    Optional<PhotoMessage> findById(PhotoMessageId photoMessageId);

    void delete(PhotoMessage photoMessage);

    /** Returns the shared photos of the circle, from the most recent to the oldest. */
    List<PhotoMessage> findAllSharedByCareCircleId(CareCircleId careCircleId);
}
