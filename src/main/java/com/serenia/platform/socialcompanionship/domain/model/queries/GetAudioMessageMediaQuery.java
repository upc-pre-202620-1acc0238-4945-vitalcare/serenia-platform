package com.serenia.platform.socialcompanionship.domain.model.queries;

import java.util.UUID;

/**
 * Query of the file of an audio, to play it.
 */
public record GetAudioMessageMediaQuery(UUID audioMessageId, UUID requesterId) {
}
