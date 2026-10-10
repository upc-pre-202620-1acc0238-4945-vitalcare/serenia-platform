package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.commands.EditSharedNoteCommand;
import com.serenia.platform.carecircle.interfaces.rest.resources.EditSharedNoteResource;

import java.util.UUID;

/**
 * Combines an {@link EditSharedNoteResource} with the note and the editor into an
 * {@link EditSharedNoteCommand}.
 */
public class EditSharedNoteCommandFromResourceAssembler {
    public static EditSharedNoteCommand toCommandFromResource(UUID sharedNoteId, UUID editorId,
                                                              EditSharedNoteResource resource) {
        return new EditSharedNoteCommand(sharedNoteId, editorId, resource.content());
    }
}
