package com.serenia.platform.iam.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Intention of updating the personal data of a user's profile.
 */
public record UpdateProfileDataCommand(
        UUID userId,
        String fullName,
        String phoneNumber,
        LocalDate birthDate,
        String locale,
        String timeZone) {
}
