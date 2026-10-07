package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

import java.util.UUID;

/**
 * Reference by identity to a circle of Care Circle.
 *
 * @param value the UUID identity
 */
public record CareCircleId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "companion.care.circle.id.null";

    public CareCircleId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }
}
