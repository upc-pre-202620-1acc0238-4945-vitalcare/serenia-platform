package com.serenia.platform.socialcompanionship.domain.model.commands;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Intention of scheduling a reminder at a local date and time.
 */
public record ScheduleSocialReminderCommand(UUID olderAdultId, String title, String description, LocalDateTime remindAt) {
}
