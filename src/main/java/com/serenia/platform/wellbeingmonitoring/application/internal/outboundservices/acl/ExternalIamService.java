package com.serenia.platform.wellbeingmonitoring.application.internal.outboundservices.acl;

import com.serenia.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Outbound service that consumes the {@link IamContextFacade} and returns the older adult's
 * time zone as a {@link ZoneId}, used to resolve the queried periods.
 */
@Service("wellbeingMonitoringExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    /** Returns the time zone of the older adult; falls back to UTC if it cannot be resolved. */
    public ZoneId fetchTimeZone(UUID olderAdultId) {
        return iamContextFacade.fetchTimeZoneById(olderAdultId)
                .<ZoneId>map(timeZone -> {
                    try {
                        return ZoneId.of(timeZone);
                    } catch (DateTimeException e) {
                        return ZoneOffset.UTC;
                    }
                })
                .orElse(ZoneOffset.UTC);
    }
}
