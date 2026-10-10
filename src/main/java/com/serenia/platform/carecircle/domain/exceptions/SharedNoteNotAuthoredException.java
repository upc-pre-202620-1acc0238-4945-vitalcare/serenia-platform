package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when someone other than the author tries to edit a shared note.
 */
public class SharedNoteNotAuthoredException extends CareCircleDomainException {
    public SharedNoteNotAuthoredException() {
        super("shared.note.not.author");
    }
}
