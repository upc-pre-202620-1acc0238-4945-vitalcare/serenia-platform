package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

/**
 * Type of alert; tells both aggregates apart in persistence.
 */
public enum AlertType {
    EMERGENCY,
    INACTIVITY
}
