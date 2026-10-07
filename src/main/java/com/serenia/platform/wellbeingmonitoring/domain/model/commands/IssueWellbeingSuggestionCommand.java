package com.serenia.platform.wellbeingmonitoring.domain.model.commands;

import java.util.UUID;

/**
 * Intention of issuing an action suggestion from a discomfort pattern.
 */
public record IssueWellbeingSuggestionCommand(UUID olderAdultId, UUID patternId) {
}
