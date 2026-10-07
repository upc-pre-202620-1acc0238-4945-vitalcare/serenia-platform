package com.serenia.platform.dailycheckin.application.internal.queryservices;

import com.serenia.platform.dailycheckin.domain.model.entities.CheckInQuestion;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInQuestionByIdQuery;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInQuestionRepository;
import com.serenia.platform.dailycheckin.domain.services.CheckInQuestionQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service that resolves the questions of the catalog.
 */
@Service
public class CheckInQuestionQueryServiceImpl implements CheckInQuestionQueryService {

    private final CheckInQuestionRepository checkInQuestionRepository;

    public CheckInQuestionQueryServiceImpl(CheckInQuestionRepository checkInQuestionRepository) {
        this.checkInQuestionRepository = checkInQuestionRepository;
    }

    @Override
    public Optional<CheckInQuestion> handle(GetCheckInQuestionByIdQuery query) {
        return checkInQuestionRepository.findById(new CheckInQuestionId(query.questionId()));
    }
}
