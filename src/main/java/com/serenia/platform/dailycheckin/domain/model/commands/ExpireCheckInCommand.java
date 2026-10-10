package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of closing a pending check-in whose deadline has passed.
 */
public record ExpireCheckInCommand(UUID checkInId) {
}
