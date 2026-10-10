package com.serenia.platform.wellbeingmonitoring.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.DismissWellbeingSuggestionCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.EvaluateWellbeingPatternCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.IssueWellbeingSuggestionCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.RecordSmallWinCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;

/**
 * Contract of the write operations of the Wellbeing Monitoring context.
 */
public interface WellbeingInsightCommandService {

    Result<Void, ApplicationError> handle(EvaluateWellbeingPatternCommand command);

    Result<WellbeingSuggestion, ApplicationError> handle(IssueWellbeingSuggestionCommand command);

    Result<SmallWin, ApplicationError> handle(RecordSmallWinCommand command);

    Result<WellbeingSuggestion, ApplicationError> handle(DismissWellbeingSuggestionCommand command);
}
