package com.serenia.platform.socialcompanionship.domain.model.queries;

import java.util.UUID;

/**
 * Query of the file of a photo, to show it.
 */
public record GetPhotoMessageMediaQuery(UUID photoMessageId, UUID requesterId) {
}
