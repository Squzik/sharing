package org.example.sharing.service;

import org.example.sharing.rest.dto.request.ReviewsFlatRequestDTO;
import org.example.sharing.rest.dto.request.ReviewsUserRequestDTO;
import org.example.sharing.rest.dto.response.ReviewsFlatResponseDTO;
import org.example.sharing.rest.dto.response.ReviewsUserResponseDTO;

import java.util.Set;
import java.util.UUID;

public interface ReviewsService {

    ReviewsUserResponseDTO addUserReview(UUID userReviewerId, ReviewsUserRequestDTO reviewsUserRequestDTO);

    Set<ReviewsUserResponseDTO> getUserReviews(UUID userId);

    ReviewsFlatResponseDTO addFlatReview(UUID userReviewerId, ReviewsFlatRequestDTO reviewsFlatRequestDTO);

    Set<ReviewsFlatResponseDTO> getFlatReviews(UUID id);

}