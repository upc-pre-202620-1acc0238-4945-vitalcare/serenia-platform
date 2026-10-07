package com.serenia.platform.carecircle.application.internal.queryservices;

import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.domain.model.queries.GetSharedNotesByCareCircleIdQuery;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.domain.repositories.SharedNoteRepository;
import com.serenia.platform.carecircle.domain.services.SharedNoteQueryService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that resolves the shared notes of a circle, checking the
 * requester's access first.
 */
@Service
public class SharedNoteQueryServiceImpl implements SharedNoteQueryService {

    private static final String NO_ACCESS = "care.circle.access.denied";

    private final SharedNoteRepository sharedNoteRepository;
    private final CareCircleRepository careCircleRepository;

    public SharedNoteQueryServiceImpl(SharedNoteRepository sharedNoteRepository, CareCircleRepository careCircleRepository) {
        this.sharedNoteRepository = sharedNoteRepository;
        this.careCircleRepository = careCircleRepository;
    }

    @Override
    public Result<List<SharedNote>, ApplicationError> handle(GetSharedNotesByCareCircleIdQuery query) {
        var careCircleId = new CareCircleId(query.careCircleId());
        var careCircle = careCircleRepository.findById(careCircleId);
        if (careCircle.isEmpty())
            return Result.failure(ApplicationError.notFound("care_circle", query.careCircleId().toString()));
        if (!careCircle.get().hasAccess(query.requesterId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        return Result.success(sharedNoteRepository.findAllByCareCircleId(careCircleId));
    }
}
