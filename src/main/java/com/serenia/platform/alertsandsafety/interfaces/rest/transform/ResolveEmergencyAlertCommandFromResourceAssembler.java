package com.serenia.platform.alertsandsafety.interfaces.rest.transform;

import com.serenia.platform.alertsandsafety.domain.model.commands.ResolveEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.ResolveAlertResource;

import java.util.UUID;

/**
 * Combines a {@link ResolveAlertResource} with the alert and the authenticated relative into a
 * {@link ResolveEmergencyAlertCommand}.
 */
public class ResolveEmergencyAlertCommandFromResourceAssembler {
    public static ResolveEmergencyAlertCommand toCommandFromResource(UUID alertId, UUID relativeId, ResolveAlertResource resource) {
        return new ResolveEmergencyAlertCommand(alertId, relativeId, resource == null ? null : resource.resolutionNote());
    }
}
