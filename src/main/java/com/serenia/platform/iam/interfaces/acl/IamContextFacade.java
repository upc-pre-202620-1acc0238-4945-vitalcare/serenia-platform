package com.serenia.platform.iam.interfaces.acl;

import java.util.Optional;
import java.util.UUID;

/**
 * Anti-Corruption Layer facade exposed by the IAM bounded context.
 *
 * <p>Care Circle uses it to check that whoever redeems a code is a distant relative;
 * Daily Check-in, Wellbeing Monitoring and Social Companionship use it to obtain the
 * time zone of the older adult; contexts with shared views use it to show names.
 * Only primitive types cross this boundary so no domain class is exposed.</p>
 */
public interface IamContextFacade {

    /**
     * Returns {@code true} if an active account exists with the given identifier.
     *
     * @param userId the account identifier
     */
    boolean existsActiveUserById(UUID userId);

    /**
     * Returns the role of the account ({@code OLDER_ADULT} or {@code DISTANT_RELATIVE}).
     *
     * @param userId the account identifier
     * @return the role name, or empty if the account does not exist
     */
    Optional<String> fetchUserRoleById(UUID userId);

    /**
     * Returns the full name of the account.
     *
     * @param userId the account identifier
     * @return the full name, or empty if the account does not exist
     */
    Optional<String> fetchFullNameById(UUID userId);

    /**
     * Returns the time zone of the account (for example, {@code America/Lima}).
     *
     * @param userId the account identifier
     * @return the time zone id, or empty if the account does not exist
     */
    Optional<String> fetchTimeZoneById(UUID userId);
}
