package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.commands.CreateSharedNoteCommand;
import com.serenia.platform.carecircle.interfaces.rest.resources.CreateSharedNoteResource;

import java.util.UUID;

/**
 * Combines a {@link CreateSharedNoteResource} with the circle and the author into a
 * {@link CreateSharedNoteCommand}.
 */
public class CreateSharedNoteCommandFromResourceAssembler {
    public static CreateSharedNoteCommand toCommandFromResource(UUID careCircleId, UUID authorId,
                                                                CreateSharedNoteResource resource) {
        return new CreateSharedNoteCommand(careCircleId, authorId, resource.content());
    }
}
