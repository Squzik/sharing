package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.ReviewsFlatRequestDTO;
import org.example.sharing.rest.dto.request.ReviewsUserRequestDTO;
import org.example.sharing.rest.dto.response.ReviewsFlatResponseDTO;
import org.example.sharing.rest.dto.response.ReviewsUserResponseDTO;
import org.example.sharing.store.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReviewsMapper {

    @Mapping(source = "user.id", target = "userId")
    ReviewsUserResponseDTO ratingToRatingUserResponseDto(Review review);

    @Mapping(source = "flat.id", target = "flatId")
    ReviewsFlatResponseDTO ratingToRatingFlatResponseDto(Review review);


    @Mapping(source = "userReviewerId", target = "reviewerUser.id")
    @Mapping(source = "reviewsUserRequestDTO.userId", target = "user.id")
    Review ratingUserRequestDtoToRating(UUID userReviewerId, ReviewsUserRequestDTO reviewsUserRequestDTO);

    @Mapping(source = "userReviewerId", target = "reviewerUser.id")
    @Mapping(source = "reviewsFlatRequestDTO.flatId", target = "flat.id")
    Review ratingFlatRequestDtoToRating(UUID userReviewerId, ReviewsFlatRequestDTO reviewsFlatRequestDTO);
}
