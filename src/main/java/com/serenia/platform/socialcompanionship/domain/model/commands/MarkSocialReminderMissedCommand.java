package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of closing as not completed a reminder whose day ended without action of the older adult.
 */
public record MarkSocialReminderMissedCommand(UUID socialReminderId) {
}
