package com.serenia.platform.socialcompanionship.application.internal.outboundservices.storage;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaKey;

import java.util.Optional;

/**
 * Outbound port for the storage of audio and image files.
 *
 * <p>Files are kept outside the database, under a random key, and are only handed out through
 * the API after the requester's access has been checked.</p>
 */
public interface MediaStorageService {

    /**
     * Stores the file under a new random key.
     *
     * @return the key to load or delete the file later
     */
    MediaKey store(MediaFile mediaFile);

    /** Loads a stored file, or empty if no file exists under the key. */
    Optional<MediaFile> load(MediaKey mediaKey);

    /** Deletes a stored file; does nothing if it does not exist. */
    void delete(MediaKey mediaKey);
}
