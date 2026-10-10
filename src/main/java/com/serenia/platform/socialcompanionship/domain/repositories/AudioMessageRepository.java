package com.serenia.platform.socialcompanionship.domain.repositories;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link AudioMessage} aggregate together with its receipts.
 */
public interface AudioMessageRepository {

    AudioMessage save(AudioMessage audioMessage);

    Optional<AudioMessage> findById(AudioMessageId audioMessageId);

    void delete(AudioMessage audioMessage);

    /** Returns the shared audios of the circle, from the most recent to the oldest. */
    List<AudioMessage> findAllSharedByCareCircleId(CareCircleId careCircleId);
}
