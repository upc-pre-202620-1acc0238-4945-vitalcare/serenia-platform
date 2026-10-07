package com.serenia.platform.dailycheckin.application.internal.outboundservices.acl;

import com.serenia.platform.carecircle.interfaces.acl.CareCircleContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Outbound service that consumes the {@link CareCircleContextFacade} to check that a relative
 * is linked to an older adult.
 */
@Service("dailyCheckInExternalCareCircleService")
public class ExternalCareCircleService {

    private final CareCircleContextFacade careCircleContextFacade;

    public ExternalCareCircleService(CareCircleContextFacade careCircleContextFacade) {
        this.careCircleContextFacade = careCircleContextFacade;
    }

    public boolean isActiveRelativeOf(UUID relativeId, UUID olderAdultId) {
        return careCircleContextFacade.isActiveRelativeOf(relativeId, olderAdultId);
    }
}
