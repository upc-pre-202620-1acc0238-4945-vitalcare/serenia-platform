package com.serenia.platform.iam.application.internal.queryservices;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.serenia.platform.iam.domain.model.queries.GetUserPhotoQuery;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.domain.repositories.UserRepository;
import com.serenia.platform.iam.domain.services.UserQueryService;
import com.serenia.platform.shared.application.outboundservices.storage.MediaStorageService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.shared.domain.model.valueobjects.MediaFile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service that resolves the read operations on the {@link User} aggregate
 * without modifying its state.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private static final String PHOTO_NOT_FOUND = "user.photo.not.found";

    private final UserRepository userRepository;
    private final MediaStorageService mediaStorageService;

    public UserQueryServiceImpl(UserRepository userRepository, MediaStorageService mediaStorageService) {
        this.userRepository = userRepository;
        this.mediaStorageService = mediaStorageService;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(new UserId(query.userId()));
    }

    @Override
    public Result<MediaFile, ApplicationError> handle(GetUserPhotoQuery query) {
        var user = userRepository.findById(new UserId(query.userId()));
        if (user.isEmpty())
            return Result.failure(ApplicationError.notFound("user", query.userId().toString()));
        if (!user.get().hasUploadedPhoto())
            return Result.failure(ApplicationError.notFound("user_photo", PHOTO_NOT_FOUND));
        return mediaStorageService.load(user.get().getPhotoKey())
                .<Result<MediaFile, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("user_photo", PHOTO_NOT_FOUND)));
    }
}
