package com.serenia.platform.iam.domain.model.commands;

import java.time.LocalDate;

/**
 * Intention of creating an account with the older adult role.
 *
 * <p>{@code password} carries the raw password; the command service hashes it
 * before invoking the aggregate factory.</p>
 */
public record RegisterOlderAdultCommand(
        String email,
        String password,
        String fullName,
        String phoneNumber,
        LocalDate birthDate,
        String locale,
        String timeZone) {
}
