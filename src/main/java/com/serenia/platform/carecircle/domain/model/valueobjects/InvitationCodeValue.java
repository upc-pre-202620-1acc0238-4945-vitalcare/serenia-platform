package com.serenia.platform.carecircle.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Value of an invitation code that the older adult shares with a relative.
 *
 * <p>Eight upper-case characters taken from an alphabet without ambiguous characters
 * (no 0, O, 1, I nor L), so the code can be dictated over the phone without mistakes.</p>
 *
 * @param value the normalized code
 */
public record InvitationCodeValue(String value) {
    public static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    public static final int LENGTH = 8;

    private static final String INVALID_MESSAGE_KEY = "invitation.code.invalid";

    private static final Pattern CODE_PATTERN = Pattern.compile("^[" + ALPHABET + "]{" + LENGTH + "}$");

    public InvitationCodeValue {
        if (value == null) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
        value = value.trim().toUpperCase(Locale.ROOT);
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
    }
}
