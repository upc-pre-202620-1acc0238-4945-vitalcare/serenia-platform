package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.commands.ReassignCareShiftCommand;
import com.serenia.platform.carecircle.interfaces.rest.resources.ReassignCareShiftResource;

import java.util.UUID;

/**
 * Combines a {@link ReassignCareShiftResource} with the shift and the requester into a
 * {@link ReassignCareShiftCommand}.
 */
public class ReassignCareShiftCommandFromResourceAssembler {
    public static ReassignCareShiftCommand toCommandFromResource(UUID careShiftId, UUID requesterId,
                                                                 ReassignCareShiftResource resource) {
        return new ReassignCareShiftCommand(careShiftId, resource.relativeId(), requesterId);
    }
}
