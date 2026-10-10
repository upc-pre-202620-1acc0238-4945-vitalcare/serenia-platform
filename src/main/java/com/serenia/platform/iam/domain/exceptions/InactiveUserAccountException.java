package com.serenia.platform.iam.domain.exceptions;

import java.util.UUID;

/**
 * Raised when an operation tries to modify a suspended or deleted account.
 */
public class InactiveUserAccountException extends RuntimeException {
    public InactiveUserAccountException(UUID userId) {
        super("User account is not active: %s".formatted(userId));
    }
}
