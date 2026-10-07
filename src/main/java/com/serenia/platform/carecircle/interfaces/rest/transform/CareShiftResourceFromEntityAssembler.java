package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.interfaces.rest.resources.CareShiftResource;

/** Converts a {@link CareShift} aggregate into its {@link CareShiftResource} REST representation. */
public class CareShiftResourceFromEntityAssembler {
    public static CareShiftResource toResourceFromEntity(CareShift careShift) {
        return new CareShiftResource(
                careShift.getId().value(),
                careShift.getCareCircleId().value(),
                careShift.getRelativeId().value(),
                careShift.getShiftDate());
    }
}
