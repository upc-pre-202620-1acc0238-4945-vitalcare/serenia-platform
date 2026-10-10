package com.serenia.platform.iam.infrastructure.persistence.jpa.entities;

import com.serenia.platform.iam.domain.model.valueobjects.AccountStatus;
import com.serenia.platform.iam.domain.model.valueobjects.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code users} table.
 *
 * <p>UUID identifiers are stored as {@code BINARY(16)}, enums as text and instants
 * as {@code DATETIME(6)} in UTC. Column lengths match the limits of the domain
 * value objects.</p>
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
public class UserPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", length = 160, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 60, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false, updatable = false)
    private UserRole role;

    @Column(name = "full_name", length = 120, nullable = false)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "locale", length = 10, nullable = false)
    private String locale;

    @Column(name = "time_zone", length = 64, nullable = false)
    private String timeZone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private AccountStatus status;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant updatedAt;
}
