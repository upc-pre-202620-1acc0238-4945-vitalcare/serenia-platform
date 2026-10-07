package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.util.UUID;

/**
 * Intention of presenting a reminder whose time arrived.
 */
public record PresentSocialReminderCommand(UUID socialReminderId) {
}
