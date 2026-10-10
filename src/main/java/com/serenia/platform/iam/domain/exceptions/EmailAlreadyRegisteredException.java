package com.serenia.platform.iam.domain.exceptions;

/**
 * Raised when an account is persisted with an email address that is already registered.
 *
 * <p>Thrown by the persistence adapter when two simultaneous registrations collide on
 * the unique email index, so the race is reported in domain terms.</p>
 */
public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("Email already registered: %s".formatted(email));
    }
}
