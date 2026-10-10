package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.PhotoMessageResource;

/** Converts the {@link PhotoMessage} aggregate into its REST representation. */
public class PhotoMessageResourceFromEntityAssembler {
    public static PhotoMessageResource toResourceFromEntity(PhotoMessage message) {
        return new PhotoMessageResource(
                message.getId().value(),
                message.getCareCircleId().value(),
                message.getSenderId().value(),
                message.getStatus().name(),
                "/api/v1/care-circles/%s/photo-messages/%s/media".formatted(message.getCareCircleId().value(), message.getId().value()),
                message.getCreatedAt(),
                message.getSentAt(),
                message.isViewed());
    }
}
