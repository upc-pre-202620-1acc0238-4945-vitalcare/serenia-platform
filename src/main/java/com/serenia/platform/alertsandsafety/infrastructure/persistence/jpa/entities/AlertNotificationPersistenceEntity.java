package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities;

import com.serenia.platform.alertsandsafety.domain.model.valueobjects.DeliveryStatus;
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
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code alert_notifications} table.
 *
 * <p>Contained in {@link AlertPersistenceEntity}; there is one notification per relative and alert.</p>
 */
@Entity
@Table(name = "alert_notifications",
        uniqueConstraints = @UniqueConstraint(name = "uk_alert_notifications_alert_relative", columnNames = {"alert_id", "relative_id"}))
@Getter
@Setter
@NoArgsConstructor
public class AlertNotificationPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "relative_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID relativeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private DeliveryStatus status;

    @Column(name = "sent_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant sentAt;

    @Column(name = "delivered_at", columnDefinition = "DATETIME(6)")
    private Instant deliveredAt;
}
