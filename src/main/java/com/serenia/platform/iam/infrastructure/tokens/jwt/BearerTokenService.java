package com.serenia.platform.iam.infrastructure.tokens.jwt;

import com.serenia.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT bearer-token service used by the authorization pipeline.
 *
 * <p>Extends {@link TokenService} with the HTTP-specific operations the filter needs,
 * keeping them out of the application layer.</p>
 */
public interface BearerTokenService extends TokenService {

    /**
     * Extracts the bearer token from the {@code Authorization} header.
     *
     * @param request incoming HTTP request
     * @return the raw token, or {@code null} if no bearer token is present
     */
    String getBearerTokenFrom(HttpServletRequest request);

    /**
     * Extracts the role carried by a valid token.
     *
     * @param token the signed token
     * @return the role name
     */
    String extractRole(String token);
}
