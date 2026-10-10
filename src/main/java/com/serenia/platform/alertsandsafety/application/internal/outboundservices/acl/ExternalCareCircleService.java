package com.serenia.platform.alertsandsafety.application.internal.outboundservices.acl;

import com.serenia.platform.carecircle.interfaces.acl.CareCircleContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Outbound service that consumes the {@link CareCircleContextFacade} to obtain the linked
 * relatives of an older adult and check links.
 */
@Service("alertsAndSafetyExternalCareCircleService")
public class ExternalCareCircleService {

    private final CareCircleContextFacade careCircleContextFacade;

    public ExternalCareCircleService(CareCircleContextFacade careCircleContextFacade) {
        this.careCircleContextFacade = careCircleContextFacade;
    }

    public List<UUID> fetchActiveRelativeIds(UUID olderAdultId) {
        return careCircleContextFacade.fetchActiveRelativeIdsByOlderAdultId(olderAdultId);
    }

    public boolean isActiveRelativeOf(UUID relativeId, UUID olderAdultId) {
        return careCircleContextFacade.isActiveRelativeOf(relativeId, olderAdultId);
    }
}
