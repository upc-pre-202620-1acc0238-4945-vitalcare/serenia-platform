package com.serenia.platform.iam.infrastructure.authorization.sfs.model;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.List;

/**
 * Builds the authenticated {@link UsernamePasswordAuthenticationToken} placed in the
 * security context for a request.
 */
public final class UsernamePasswordAuthenticationTokenBuilder {

    private static final String ROLE_PREFIX = "ROLE_";

    private UsernamePasswordAuthenticationTokenBuilder() {
    }

    /**
     * Creates an authenticated token whose single authority is the user's role.
     *
     * @param principal the authenticated principal
     * @param request   the current request, used for the web authentication details
     * @return the authentication token (credentials are {@code null})
     */
    public static UsernamePasswordAuthenticationToken build(AuthenticatedUserPrincipal principal,
                                                            HttpServletRequest request) {
        var authorities = List.of(new SimpleGrantedAuthority(ROLE_PREFIX + principal.role()));
        var token = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return token;
    }
}
