package com.serenia.platform.carecircle.application.internal.outboundservices.acl;

import com.serenia.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound service that consumes the {@link IamContextFacade} and translates its answers
 * into Care Circle types, such as {@link ZoneId}.
 */
// Explicit name avoids conflicts with the ExternalIamService of sibling bounded contexts
@Service("careCircleExternalIamService")
public class ExternalIamService {

    private static final String DISTANT_RELATIVE_ROLE = "DISTANT_RELATIVE";

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    /** Returns the role of the user, or empty if the account does not exist. */
    public Optional<String> fetchUserRole(UUID userId) {
        return iamContextFacade.fetchUserRoleById(userId);
    }

    /** Indicates whether the user has an active account. */
    public boolean isActiveUser(UUID userId) {
        return iamContextFacade.existsActiveUserById(userId);
    }

    /** Indicates whether the user has an active account with the distant relative role. */
    public boolean isActiveDistantRelative(UUID userId) {
        return isActiveUser(userId)
                && fetchUserRole(userId).map(DISTANT_RELATIVE_ROLE::equals).orElse(false);
    }

    /** Returns the time zone of the user, or empty if the account does not exist or its zone is invalid. */
    public Optional<ZoneId> fetchTimeZone(UUID userId) {
        return iamContextFacade.fetchTimeZoneById(userId).flatMap(timeZone -> {
            try {
                return Optional.of(ZoneId.of(timeZone));
            } catch (DateTimeException e) {
                return Optional.empty();
            }
        });
    }
}
