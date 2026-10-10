package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

import java.util.UUID;

/**
 * Reference by identity to a distant relative account of Identity &amp; Access.
 *
 * @param value the UUID identity
 */
public record RelativeId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "alert.relative.id.null";

    public RelativeId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }
}
