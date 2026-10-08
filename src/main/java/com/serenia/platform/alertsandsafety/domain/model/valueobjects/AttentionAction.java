package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

/**
 * Action of a relative on an alert; each alert admits a single acknowledgement and a single resolution.
 */
public enum AttentionAction {
    ACKNOWLEDGED,
    RESOLVED
}
