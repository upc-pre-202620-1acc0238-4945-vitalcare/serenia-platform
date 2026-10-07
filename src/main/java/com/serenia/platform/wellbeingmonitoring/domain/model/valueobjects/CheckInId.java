package com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects;

import java.util.UUID;

/**
 * Reference by identity to a check-in of Daily Check-in.
 *
 * @param value the UUID identity
 */
public record CheckInId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "wellbeing.check.in.id.null";

    public CheckInId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }
}
