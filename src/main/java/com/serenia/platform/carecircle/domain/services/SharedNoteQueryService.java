package com.serenia.platform.carecircle.domain.services;

import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.domain.model.queries.GetSharedNotesByCareCircleIdQuery;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

import java.util.List;

/**
 * Contract of the read operations on shared notes.
 */
public interface SharedNoteQueryService {

    Result<List<SharedNote>, ApplicationError> handle(GetSharedNotesByCareCircleIdQuery query);
}
