package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Duration;

/**
 * Turns a loaded media file into the HTTP response that serves it.
 *
 * <p>The body is a {@link org.springframework.core.io.Resource}, so Spring MVC answers byte-range
 * requests with 206 Partial Content and the player can seek within an audio. The response is
 * only cacheable by the requester's browser, since the file is private.</p>
 */
public final class MediaResponseAssembler {

    private MediaResponseAssembler() {
    }

    public static ResponseEntity<?> toResponseEntityFromResult(Result<MediaFile, ApplicationError> result) {
        return switch (result) {
            case Result.Success<MediaFile, ApplicationError> success -> ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(success.value().contentType()))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePrivate())
                    .body(new ByteArrayResource(success.value().content()));
            case Result.Failure<MediaFile, ApplicationError> failure -> {
                // The media endpoints produce any type, so the error body needs an explicit JSON type
                var error = ErrorResponseAssembler.toErrorResponseFromApplicationError(failure.error());
                yield ResponseEntity.status(error.getStatusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(error.getBody());
            }
        };
    }
}
