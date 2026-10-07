package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities;

import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInStatus;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.MoodLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
 * JPA persistence entity for a row of the {@code check_ins} table.
 *
 * <p>The unique {@code (older_adult_id, check_date)} index guarantees one check-in per day and
 * resolves the queries by date; the {@code (status, deadline_at)} index resolves the searches
 * of the periodic jobs.</p>
 */
@Entity
@Table(name = "check_ins",
        uniqueConstraints = @UniqueConstraint(name = "uk_check_ins_older_adult_check_date",
                columnNames = {"older_adult_id", "check_date"}),
        indexes = @Index(name = "idx_check_ins_status_deadline_at", columnList = "status, deadline_at"))
@Getter
@Setter
@NoArgsConstructor
public class CheckInPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "question_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID questionId;

    @Column(name = "check_date", nullable = false, updatable = false)
    private LocalDate checkDate;

    @Column(name = "scheduled_for", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant scheduledFor;

    @Column(name = "deadline_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant deadlineAt;

    @Column(name = "prompted_at", columnDefinition = "DATETIME(6)")
    private Instant promptedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood", length = 20)
    private MoodLevel mood;

    @Column(name = "positive_activity", length = 200)
    private String positiveActivity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CheckInStatus status;

    @Column(name = "answered_at", columnDefinition = "DATETIME(6)")
    private Instant answeredAt;
}
