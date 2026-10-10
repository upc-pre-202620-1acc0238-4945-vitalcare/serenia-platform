package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.commands.AssignCareShiftCommand;
import com.serenia.platform.carecircle.interfaces.rest.resources.AssignCareShiftResource;

import java.util.UUID;

/**
 * Combines an {@link AssignCareShiftResource} with the circle and the authenticated relative
 * into an {@link AssignCareShiftCommand}.
 */
public class AssignCareShiftCommandFromResourceAssembler {
    public static AssignCareShiftCommand toCommandFromResource(UUID careCircleId, UUID relativeId,
                                                               AssignCareShiftResource resource) {
        return new AssignCareShiftCommand(careCircleId, relativeId, resource.shiftDate());
    }
}
