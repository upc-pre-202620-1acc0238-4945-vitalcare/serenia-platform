package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

/**
 * Duration of an audio message.
 *
 * @param seconds the duration, between 1 and 180 seconds
 */
public record AudioDuration(int seconds) {
    public static final int MIN_SECONDS = 1;
    public static final int MAX_SECONDS = 180;

    private static final String OUT_OF_RANGE_MESSAGE_KEY = "audio.message.duration.out.of.range";

    public AudioDuration {
        if (seconds < MIN_SECONDS || seconds > MAX_SECONDS) {
            throw new IllegalArgumentException(OUT_OF_RANGE_MESSAGE_KEY);
        }
    }
}
