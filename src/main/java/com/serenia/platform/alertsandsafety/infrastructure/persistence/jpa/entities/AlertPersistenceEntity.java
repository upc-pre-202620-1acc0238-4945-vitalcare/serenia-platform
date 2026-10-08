package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities;

import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertStatus;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code alerts} table, whose type tells whether it is
 * an emergency or an inactivity alert.
 *
 * <p>Contains the collections of notifications and attention actions, persisted in cascade with
 * the alert. The {@code (older_adult_id, triggered_at)} index resolves the history of an older
 * adult; the unique index on {@code check_in_id} prevents two alerts for the same check-in.</p>
 */
@Entity
@Table(name = "alerts",
        uniqueConstraints = @UniqueConstraint(name = "uk_alerts_check_in_id", columnNames = "check_in_id"),
        indexes = @Index(name = "idx_alerts_older_adult_triggered_at", columnList = "older_adult_id, triggered_at"))
@Getter
@Setter
@NoArgsConstructor
public class AlertPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false, updatable = false)
    private AlertType type;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    /** Only filled for inactivity alerts. */
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "check_in_id", columnDefinition = "BINARY(16)", updatable = false)
    private UUID checkInId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private AlertStatus status;

    @Column(name = "triggered_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant triggeredAt;

    @Column(name = "resolved_at", columnDefinition = "DATETIME(6)")
    private Instant resolvedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "alert_id", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("sentAt ASC")
    private List<AlertNotificationPersistenceEntity> notifications = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "alert_id", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("actedAt ASC")
    private List<AlertAttentionPersistenceEntity> attentions = new ArrayList<>();
}
