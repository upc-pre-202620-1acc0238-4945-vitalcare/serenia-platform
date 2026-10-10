package com.serenia.platform.dailycheckin.interfaces.acl;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Anti-Corruption Layer facade exposed by the Daily Check-in bounded context.
 *
 * <p>Wellbeing Monitoring uses it to obtain the moods of several consecutive days when
 * evaluating patterns. Only standard Java types cross this boundary so no domain class is exposed.</p>
 */
public interface DailyCheckInContextFacade {

    /**
     * Returns the moods reported in the answered check-ins between both dates, inclusive.
     *
     * @return the mood level name by check-in date, ordered by date; days without an answer are absent
     */
    Map<LocalDate, String> fetchMoodsByDateRange(UUID olderAdultId, LocalDate fromDate, LocalDate toDate);
}
