package com.serenia.platform.carecircle.domain.services;

import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.domain.model.commands.CreateSharedNoteCommand;
import com.serenia.platform.carecircle.domain.model.commands.EditSharedNoteCommand;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on shared notes.
 */
public interface SharedNoteCommandService {

    Result<SharedNote, ApplicationError> handle(CreateSharedNoteCommand command);

    Result<SharedNote, ApplicationError> handle(EditSharedNoteCommand command);
}
