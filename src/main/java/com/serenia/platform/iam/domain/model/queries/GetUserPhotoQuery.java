package com.serenia.platform.iam.domain.model.queries;

import java.util.UUID;

/**
 * Query of the uploaded profile photo of an account.
 *
 * @param userId the identifier of the account
 */
public record GetUserPhotoQuery(UUID userId) {
}
