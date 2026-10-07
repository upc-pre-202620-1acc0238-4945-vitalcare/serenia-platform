package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of canceling a reminder that is no longer needed.
 */
public record CancelSocialReminderCommand(UUID socialReminderId, UUID olderAdultId) {
}
