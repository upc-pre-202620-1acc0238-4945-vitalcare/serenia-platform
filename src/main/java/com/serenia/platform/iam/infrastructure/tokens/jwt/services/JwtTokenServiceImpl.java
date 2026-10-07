package com.serenia.platform.iam.infrastructure.tokens.jwt.services;

import com.serenia.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

/**
 * JWT implementation of {@link BearerTokenService}.
 *
 * <p>Issues HMAC-SHA signed tokens with the {@code JWT_SECRET} key, carrying the user id
 * as subject plus the session id, the role and the expiration. The token hash is computed
 * with SHA-256: it is checked on every request and the token has enough entropy not to
 * need a slow algorithm such as BCrypt.</p>
 */
@Service
@Slf4j
public class JwtTokenServiceImpl implements BearerTokenService {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String SESSION_ID_CLAIM = "sid";
    private static final String ROLE_CLAIM = "role";

    private final SecretKey signingKey;

    public JwtTokenServiceImpl(@Value("${authorization.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(UUID userId, UUID sessionId, String role, Instant expiresAt) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim(SESSION_ID_CLAIM, sessionId.toString())
                .claim(ROLE_CLAIM, role)
                .issuedAt(new Date())
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JSON Web Token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public UUID extractSessionId(String token) {
        return UUID.fromString(extractAllClaims(token).get(SESSION_ID_CLAIM, String.class));
    }

    @Override
    public String extractRole(String token) {
        return extractAllClaims(token).get(ROLE_CLAIM, String.class);
    }

    @Override
    public String hash(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        var header = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_TOKEN_PREFIX)) {
            return header.substring(BEARER_TOKEN_PREFIX.length());
        }
        return null;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
