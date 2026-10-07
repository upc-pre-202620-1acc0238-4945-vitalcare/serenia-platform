package com.serenia.platform.socialcompanionship.infrastructure.storage.local.services;

import com.serenia.platform.socialcompanionship.application.internal.outboundservices.storage.MediaStorageService;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

/**
 * Stores the files in a local directory, under a random key. Meant for development, so the
 * platform runs without an Azure account; the files are also only handed out through the API.
 */
@Service
@ConditionalOnProperty(prefix = "social-companionship.media", name = "storage-provider", havingValue = "local",
        matchIfMissing = true)
public class LocalFileSystemMediaStorageService implements MediaStorageService {

    private static final String INVALID_KEY_MESSAGE_KEY = "media.key.invalid";

    private final Path directory;

    public LocalFileSystemMediaStorageService(@Value("${social-companionship.media.local-directory}") String directory) {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create the media directory " + this.directory, e);
        }
    }

    @Override
    public MediaKey store(MediaFile mediaFile) {
        var key = new MediaKey(UUID.randomUUID() + "." + mediaFile.extension());
        try {
            Files.write(resolve(key), mediaFile.content());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot store the media file " + key.value(), e);
        }
        return key;
    }

    @Override
    public Optional<MediaFile> load(MediaKey mediaKey) {
        var path = resolve(mediaKey);
        if (!Files.isRegularFile(path)) return Optional.empty();
        try {
            var contentType = MediaFile.contentTypeOf(mediaKey).orElse("application/octet-stream");
            return Optional.of(new MediaFile(Files.readAllBytes(path), contentType));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load the media file " + mediaKey.value(), e);
        }
    }

    @Override
    public void delete(MediaKey mediaKey) {
        try {
            Files.deleteIfExists(resolve(mediaKey));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot delete the media file " + mediaKey.value(), e);
        }
    }

    /** Resolves the key inside the media directory, rejecting keys that would escape it. */
    private Path resolve(MediaKey mediaKey) {
        var path = directory.resolve(mediaKey.value()).normalize();
        if (!path.getParent().equals(directory)) throw new IllegalArgumentException(INVALID_KEY_MESSAGE_KEY);
        return path;
    }
}
