package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.interfaces.rest.resources.CareCircleResource;

/** Converts a {@link CareCircle} aggregate into its {@link CareCircleResource} REST representation. */
public class CareCircleResourceFromEntityAssembler {
    public static CareCircleResource toResourceFromEntity(CareCircle careCircle) {
        return new CareCircleResource(
                careCircle.getId().value(),
                careCircle.getOlderAdultId().value(),
                careCircle.getCreatedAt());
    }
}
