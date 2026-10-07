package com.serenia.platform.iam.infrastructure.authorization.sfs.pipeline;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point invoked when an unauthenticated request reaches a protected resource.
 *
 * <p>Answers 401 Unauthorized instead of redirecting to a login page, as expected
 * from a stateless REST API.</p>
 */
@Component
@Slf4j
public class UnauthorizedRequestHandlerEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        log.info("Unauthorized request to {}: {}", request.getRequestURI(), authenticationException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized request detected");
    }
}
