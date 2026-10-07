package com.serenia.platform.socialcompanionship.interfaces.rest.transform;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.AudioMessageResource;

import java.util.UUID;

/** Converts the {@link AudioMessage} aggregate into its REST representation for the given requester. */
public class AudioMessageResourceFromEntityAssembler {
    public static AudioMessageResource toResourceFromEntity(AudioMessage message, UUID requesterId) {
        return new AudioMessageResource(
                message.getId().value(),
                message.getCareCircleId().value(),
                message.getSenderId().value(),
                message.getStatus().name(),
                message.getDuration().seconds(),
                "/api/v1/care-circles/%s/audio-messages/%s/media".formatted(message.getCareCircleId().value(), message.getId().value()),
                message.getCreatedAt(),
                message.getSentAt(),
                message.isPlayedBy(requesterId));
    }
}
