package com.serenia.platform.dailycheckin.domain.services;

import com.serenia.platform.dailycheckin.domain.model.entities.CheckInQuestion;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInQuestionByIdQuery;

import java.util.Optional;

/**
 * Contract of the read operations on the question catalog.
 */
public interface CheckInQuestionQueryService {

    Optional<CheckInQuestion> handle(GetCheckInQuestionByIdQuery query);
}
