package com.serenia.platform.wellbeingmonitoring.interfaces.rest.transform;

import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.interfaces.rest.resources.SmallWinResource;

import java.util.UUID;

/**
 * Converts a {@link SmallWin} entity into its {@link SmallWinResource} REST representation.
 * The older adult is passed apart because it is the identity of the aggregate.
 */
public class SmallWinResourceFromEntityAssembler {
    public static SmallWinResource toResourceFromEntity(UUID olderAdultId, SmallWin smallWin) {
        return new SmallWinResource(
                smallWin.getId().value(),
                olderAdultId,
                smallWin.getCheckInId().value(),
                smallWin.getDescription().value(),
                smallWin.getRecordedAt());
    }
}
