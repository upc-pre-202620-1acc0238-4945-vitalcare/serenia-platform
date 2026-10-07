package com.serenia.platform.carecircle.domain.model.valueobjects;

/**
 * Relationship declared by a relative with the older adult, for example "hija".
 *
 * @param value the trimmed label
 */
public record RelationshipLabel(String value) {
    public static final int MAX_LENGTH = 60;

    private static final String NOT_BLANK_MESSAGE_KEY = "family.link.relationship.label.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "family.link.relationship.label.too.long";

    public RelationshipLabel {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }

    /**
     * Builds a relationship label from an optional raw value.
     *
     * @param value raw label, possibly {@code null} or blank
     * @return the label, or {@code null} when no value was provided
     */
    public static RelationshipLabel fromNullable(String value) {
        return value == null || value.isBlank() ? null : new RelationshipLabel(value);
    }
}
