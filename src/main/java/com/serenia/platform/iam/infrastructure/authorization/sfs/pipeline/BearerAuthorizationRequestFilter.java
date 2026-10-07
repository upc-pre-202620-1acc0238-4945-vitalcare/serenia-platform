package com.serenia.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.serenia.platform.iam.domain.model.queries.GetActiveSessionByIdQuery;
import com.serenia.platform.iam.domain.model.valueobjects.TokenHash;
import com.serenia.platform.iam.domain.services.SessionQueryService;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.serenia.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that authenticates every request carrying a bearer token.
 *
 * <p>Validates the token signature and expiration, extracts the session id, and checks
 * that the session is still active and that its stored hash matches the presented token.
 * When the session was closed or revoked the request stays unauthenticated, so protected
 * endpoints answer 401 Unauthorized. When valid, the user and role are placed in the
 * security context without loading the full account.</p>
 *
 * <p>Not annotated with {@code @Component}: it is registered explicitly in
 * {@link com.serenia.platform.iam.infrastructure.authorization.sfs.configuration.WebSecurityConfiguration}.</p>
 */
@Slf4j
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private final BearerTokenService tokenService;
    private final SessionQueryService sessionQueryService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, SessionQueryService sessionQueryService) {
        this.tokenService = tokenService;
        this.sessionQueryService = sessionQueryService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        var token = tokenService.getBearerTokenFrom(request);
        if (token != null && tokenService.validateToken(token)) {
            authenticate(token, request);
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String token, HttpServletRequest request) {
        try {
            var sessionId = tokenService.extractSessionId(token);
            var tokenHash = new TokenHash(tokenService.hash(token));
            var session = sessionQueryService.handle(new GetActiveSessionByIdQuery(sessionId))
                    .filter(activeSession -> activeSession.matchesToken(tokenHash));
            if (session.isEmpty()) {
                log.info("Session {} is closed, revoked or does not match the token", sessionId);
                return;
            }
            var principal = new AuthenticatedUserPrincipal(
                    session.get().getUserId().value(), sessionId, tokenService.extractRole(token));
            SecurityContextHolder.getContext().setAuthentication(
                    UsernamePasswordAuthenticationTokenBuilder.build(principal, request));
        } catch (RuntimeException e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
    }
}
