package com.serenia.platform.iam.domain.model.queries;

import java.util.UUID;

/**
 * Query of an account by its identifier.
 *
 * @param userId the identifier of the account
 */
public record GetUserByIdQuery(UUID userId) {
}
