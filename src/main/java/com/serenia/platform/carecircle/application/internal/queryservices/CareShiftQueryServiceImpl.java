package com.serenia.platform.carecircle.application.internal.queryservices;

import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.domain.model.queries.GetCareShiftsByDateRangeQuery;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.domain.repositories.CareShiftRepository;
import com.serenia.platform.carecircle.domain.services.CareShiftQueryService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that resolves the care shift calendar of a circle, checking the
 * requester's access first.
 */
@Service
public class CareShiftQueryServiceImpl implements CareShiftQueryService {

    private static final String NO_ACCESS = "care.circle.access.denied";

    private final CareShiftRepository careShiftRepository;
    private final CareCircleRepository careCircleRepository;

    public CareShiftQueryServiceImpl(CareShiftRepository careShiftRepository, CareCircleRepository careCircleRepository) {
        this.careShiftRepository = careShiftRepository;
        this.careCircleRepository = careCircleRepository;
    }

    @Override
    public Result<List<CareShift>, ApplicationError> handle(GetCareShiftsByDateRangeQuery query) {
        var careCircleId = new CareCircleId(query.careCircleId());
        var careCircle = careCircleRepository.findById(careCircleId);
        if (careCircle.isEmpty())
            return Result.failure(ApplicationError.notFound("care_circle", query.careCircleId().toString()));
        if (!careCircle.get().hasAccess(query.requesterId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        return Result.success(careShiftRepository.findAllByCareCircleIdAndShiftDateBetween(
                careCircleId, query.fromDate(), query.toDate()));
    }
}
