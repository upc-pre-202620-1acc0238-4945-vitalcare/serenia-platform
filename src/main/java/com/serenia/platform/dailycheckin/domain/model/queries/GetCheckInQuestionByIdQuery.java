package com.serenia.platform.dailycheckin.domain.model.queries;

import java.util.UUID;

/**
 * Query of a question of the catalog.
 */
public record GetCheckInQuestionByIdQuery(UUID questionId) {
}
