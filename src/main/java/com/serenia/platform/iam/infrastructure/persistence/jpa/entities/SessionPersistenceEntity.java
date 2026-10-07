package com.serenia.platform.iam.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code sessions} table.
 *
 * <p>The owner is stored by identity only, indexed to resolve the active sessions
 * of a user when they are revoked.</p>
 */
@Entity
@Table(name = "sessions", indexes = @Index(name = "idx_sessions_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class SessionPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "token_hash", columnDefinition = "CHAR(64)", nullable = false, updatable = false)
    private String tokenHash;

    @Column(name = "device_info", length = 200)
    private String deviceInfo;

    @Column(name = "issued_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at", columnDefinition = "DATETIME(6)")
    private Instant revokedAt;
}
