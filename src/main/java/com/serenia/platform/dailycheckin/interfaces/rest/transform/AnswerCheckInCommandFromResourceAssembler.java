package com.serenia.platform.dailycheckin.interfaces.rest.transform;

import com.serenia.platform.dailycheckin.domain.model.commands.AnswerCheckInCommand;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.AnswerCheckInResource;

import java.util.UUID;

/**
 * Combines an {@link AnswerCheckInResource} with the check-in and the authenticated older adult
 * into an {@link AnswerCheckInCommand}.
 */
public class AnswerCheckInCommandFromResourceAssembler {
    public static AnswerCheckInCommand toCommandFromResource(UUID checkInId, UUID olderAdultId, AnswerCheckInResource resource) {
        return new AnswerCheckInCommand(checkInId, olderAdultId, resource.mood(), resource.positiveActivity());
    }
}
