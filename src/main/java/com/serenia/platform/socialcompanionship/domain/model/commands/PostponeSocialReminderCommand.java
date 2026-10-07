package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of postponing a presented reminder.
 */
public record PostponeSocialReminderCommand(UUID socialReminderId, UUID olderAdultId) {
}
