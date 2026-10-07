package com.serenia.platform.carecircle.domain.services;

import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.domain.model.commands.AssignCareShiftCommand;
import com.serenia.platform.carecircle.domain.model.commands.ReassignCareShiftCommand;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on care shifts.
 */
public interface CareShiftCommandService {

    Result<CareShift, ApplicationError> handle(AssignCareShiftCommand command);

    Result<CareShift, ApplicationError> handle(ReassignCareShiftCommand command);
}
