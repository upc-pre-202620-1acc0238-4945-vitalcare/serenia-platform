package com.serenia.platform.iam.domain.model.queries;

import java.util.UUID;

/**
 * Query of a session that is still active; used by the authorization filter
 * on every request.
 *
 * @param sessionId the identifier of the session
 */
public record GetActiveSessionByIdQuery(UUID sessionId) {
}
