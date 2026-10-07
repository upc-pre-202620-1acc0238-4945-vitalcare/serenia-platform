package com.serenia.platform.wellbeingmonitoring.domain.model.entities;

import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.CheckInId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SmallWinDescription;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SmallWinId;
import lombok.Getter;

import java.time.Instant;

/**
 * Good day or positive activity of the older adult, shown to the relatives to reinforce
 * the positive. Each check-in produces at most one small win.
 *
 * <p>Belongs to the {@link com.serenia.platform.wellbeingmonitoring.domain.model.aggregates.WellbeingInsight}
 * aggregate.</p>
 */
@Getter
public class SmallWin {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "small.win.required.value";

    private final SmallWinId id;
    private final CheckInId checkInId;
    private final SmallWinDescription description;
    private final Instant recordedAt;

    public SmallWin(SmallWinId id, CheckInId checkInId, SmallWinDescription description, Instant recordedAt) {
        if (id == null || checkInId == null || description == null || recordedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.checkInId = checkInId;
        this.description = description;
        this.recordedAt = recordedAt;
    }

    public static SmallWin record(CheckInId checkInId, SmallWinDescription description, Instant recordedAt) {
        return new SmallWin(SmallWinId.generate(), checkInId, description, recordedAt);
    }
}
