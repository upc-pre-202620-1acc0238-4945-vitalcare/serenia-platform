package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

/** Reads an uploaded multipart file into a {@link MediaFile}. */
public final class MediaFileFromMultipartFileAssembler {

    private MediaFileFromMultipartFileAssembler() {
    }

    public static MediaFile toMediaFile(MultipartFile file) {
        try {
            return new MediaFile(file.getBytes(), file.getContentType());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read the uploaded file", e);
        }
    }
}
