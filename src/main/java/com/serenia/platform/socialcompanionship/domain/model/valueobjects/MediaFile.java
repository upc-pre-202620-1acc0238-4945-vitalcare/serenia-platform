package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * File received to be stored, or loaded back from the media storage.
 *
 * <p>Only AAC audio ({@code audio/mp4}) and JPEG or PNG images are supported. The declared
 * content type must match the file signature, so a file cannot pass as another type; the
 * maximum size is configured and checked by the application layer.</p>
 *
 * @param content     the bytes of the file
 * @param contentType the media type of the file
 */
public record MediaFile(byte[] content, String contentType) {
    public static final String AAC_AUDIO = "audio/mp4";
    public static final String JPEG_IMAGE = "image/jpeg";
    public static final String PNG_IMAGE = "image/png";

    private static final Map<String, String> EXTENSIONS = Map.of(AAC_AUDIO, "m4a", JPEG_IMAGE, "jpg", PNG_IMAGE, "png");

    private static final String EMPTY_MESSAGE_KEY = "media.file.empty";

    public MediaFile {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException(EMPTY_MESSAGE_KEY);
        }
        content = content.clone();
        contentType = contentType == null ? "" : contentType.trim().toLowerCase(Locale.ROOT);
    }

    /** Indicates whether the file is a supported audio: AAC in an MP4 container. */
    public boolean isSupportedAudio() {
        // MP4 files carry the "ftyp" box type at bytes 4 to 7
        return AAC_AUDIO.equals(contentType) && content.length > 8
                && content[4] == 'f' && content[5] == 't' && content[6] == 'y' && content[7] == 'p';
    }

    /** Indicates whether the file is a supported image: JPEG or PNG. */
    public boolean isSupportedImage() {
        return switch (contentType) {
            case JPEG_IMAGE -> startsWith(0xFF, 0xD8, 0xFF);
            case PNG_IMAGE -> startsWith(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A);
            default -> false;
        };
    }

    public long size() {
        return content.length;
    }

    /** Returns the file extension used for the storage key of this type. */
    public String extension() {
        return EXTENSIONS.getOrDefault(contentType, "bin");
    }

    /** Returns the content type that corresponds to the extension of a storage key. */
    public static Optional<String> contentTypeOf(MediaKey key) {
        var dot = key.value().lastIndexOf('.');
        if (dot < 0) return Optional.empty();
        var extension = key.value().substring(dot + 1).toLowerCase(Locale.ROOT);
        return EXTENSIONS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(extension))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Override
    public byte[] content() {
        return content.clone();
    }

    private boolean startsWith(int... signature) {
        if (content.length < signature.length) return false;
        for (int i = 0; i < signature.length; i++) {
            if ((content[i] & 0xFF) != signature[i]) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MediaFile file && contentType.equals(file.contentType) && Arrays.equals(content, file.content);
    }

    @Override
    public int hashCode() {
        return 31 * contentType.hashCode() + Arrays.hashCode(content);
    }

    @Override
    public String toString() {
        return "MediaFile[contentType=%s, size=%d]".formatted(contentType, content.length);
    }
}
