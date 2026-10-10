package com.serenia.platform.dailycheckin.domain.repositories;

import com.serenia.platform.dailycheckin.domain.model.entities.CheckInQuestion;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;

import java.util.List;
import java.util.Optional;

/**
 * Read contract of the question catalog; {@link #saveAll} is only used by the initial load.
 */
public interface CheckInQuestionRepository {

    Optional<CheckInQuestion> findById(CheckInQuestionId questionId);

    List<CheckInQuestion> findAllActive();

    void saveAll(List<CheckInQuestion> questions);

    long count();
}
