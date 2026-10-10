package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.socialcompanionship.domain.model.commands.ScheduleSocialReminderCommand;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.ScheduleSocialReminderResource;

import java.util.UUID;

/**
 * Combines a {@link ScheduleSocialReminderResource} with the authenticated older adult into a
 * {@link ScheduleSocialReminderCommand}.
 */
public class ScheduleSocialReminderCommandFromResourceAssembler {
    public static ScheduleSocialReminderCommand toCommandFromResource(UUID olderAdultId, ScheduleSocialReminderResource resource) {
        return new ScheduleSocialReminderCommand(olderAdultId, resource.title(), resource.description(), resource.remindAt());
    }
}
