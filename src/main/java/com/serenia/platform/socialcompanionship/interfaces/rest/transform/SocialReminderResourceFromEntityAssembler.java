package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.SocialReminderResource;

import java.time.ZoneId;

/**
 * Converts the {@link SocialReminder} aggregate into its REST representation, expressing the
 * time in the older adult's time zone.
 */
public class SocialReminderResourceFromEntityAssembler {
    public static SocialReminderResource toResourceFromEntity(SocialReminder reminder, ZoneId timeZone) {
        return new SocialReminderResource(
                reminder.getId().value(),
                reminder.getOlderAdultId().value(),
                reminder.getTitle().value(),
                reminder.getDescription() == null ? null : reminder.getDescription().value(),
                reminder.getRemindAt().atZone(timeZone).toLocalDateTime(),
                reminder.getStatus().name(),
                reminder.getCompletedAt());
    }
}
