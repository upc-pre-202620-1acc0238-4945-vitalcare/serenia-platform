package com.serenia.platform.dailycheckin.interfaces.rest.transform;

import com.serenia.platform.dailycheckin.domain.model.commands.ScheduleCheckInCommand;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.ScheduleCheckInResource;

import java.util.UUID;

/** Converts a {@link ScheduleCheckInResource} into a {@link ScheduleCheckInCommand}. */
public class ScheduleCheckInCommandFromResourceAssembler {
    public static ScheduleCheckInCommand toCommandFromResource(UUID olderAdultId, ScheduleCheckInResource resource) {
        return new ScheduleCheckInCommand(olderAdultId, resource.reminderTime());
    }
}
