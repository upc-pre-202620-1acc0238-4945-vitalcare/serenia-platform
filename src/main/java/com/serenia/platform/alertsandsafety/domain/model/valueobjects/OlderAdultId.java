package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

import java.util.UUID;

/**
 * Reference by identity to an older adult account of Identity &amp; Access.
 *
 * @param value the UUID identity
 */
public record OlderAdultId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "alert.older.adult.id.null";

    public OlderAdultId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }
}
