package com.serenia.platform.dailycheckin.interfaces.rest.transform;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.CheckInResource;

/**
 * Combines the {@link CheckIn} aggregate with the text of its question and the status of the
 * pause into its {@link CheckInResource} REST representation.
 */
public class CheckInResourceFromEntityAssembler {
    public static CheckInResource toResourceFromEntity(CheckIn checkIn, String questionText, boolean paused) {
        return new CheckInResource(
                checkIn.getId().value(),
                checkIn.getOlderAdultId().value(),
                checkIn.getCheckDate(),
                questionText,
                checkIn.getWindow().scheduledFor(),
                checkIn.getWindow().deadlineAt(),
                checkIn.getStatus().name(),
                paused,
                checkIn.getMood() == null ? null : checkIn.getMood().name(),
                checkIn.getPositiveActivity() == null ? null : checkIn.getPositiveActivity().value(),
                checkIn.getAnsweredAt());
    }
}
