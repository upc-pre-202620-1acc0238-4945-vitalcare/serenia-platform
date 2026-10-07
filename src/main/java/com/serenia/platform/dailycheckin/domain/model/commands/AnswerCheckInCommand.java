package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of the older adult to answer the check-in.
 */
public record AnswerCheckInCommand(UUID checkInId, UUID olderAdultId, String mood, String positiveActivity) {
}
