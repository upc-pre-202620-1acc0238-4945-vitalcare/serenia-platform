package com.serenia.platform.alertsandsafety.interfaces.rest.transform;

import com.serenia.platform.alertsandsafety.domain.model.commands.ResolveInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.ResolveAlertResource;

import java.util.UUID;

/**
 * Combines a {@link ResolveAlertResource} with the alert and the authenticated relative into a
 * {@link ResolveInactivityAlertCommand}.
 */
public class ResolveInactivityAlertCommandFromResourceAssembler {
    public static ResolveInactivityAlertCommand toCommandFromResource(UUID alertId, UUID relativeId, ResolveAlertResource resource) {
        return new ResolveInactivityAlertCommand(alertId, relativeId, resource == null ? null : resource.resolutionNote());
    }
}
