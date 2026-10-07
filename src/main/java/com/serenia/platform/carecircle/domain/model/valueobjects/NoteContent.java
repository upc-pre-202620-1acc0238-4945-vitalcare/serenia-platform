package com.serenia.platform.carecircle.domain.model.valueobjects;

import java.nio.charset.StandardCharsets;

/**
 * Content of a shared note.
 *
 * <p>It cannot be blank nor exceed the 65 535 bytes of the {@code TEXT} column.</p>
 *
 * @param value the trimmed content
 */
public record NoteContent(String value) {
    public static final int MAX_BYTES = 65_535;

    private static final String NOT_BLANK_MESSAGE_KEY = "shared.note.content.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "shared.note.content.too.long";

    public NoteContent {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }
}
