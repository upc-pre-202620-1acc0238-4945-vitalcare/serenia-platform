package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of marking a reminder as done.
 */
public record CompleteSocialReminderCommand(UUID socialReminderId, UUID olderAdultId) {
}
