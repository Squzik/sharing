package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.ReviewsFlatRequestDTO;
import org.example.sharing.rest.dto.request.ReviewsUserRequestDTO;
import org.example.sharing.rest.dto.response.ReviewsFlatResponseDTO;
import org.example.sharing.rest.dto.response.ReviewsUserResponseDTO;
import org.example.sharing.rest.mapper.ReviewsMapper;
import org.example.sharing.service.ReviewsService;
import org.example.sharing.store.entity.Review;
import org.example.sharing.store.repository.ReviewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewsServiceImpl implements ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final ReviewsMapper reviewsMapper;

    /**
     * Добавление рейтинга пользователю
     *
     * @param reviewsUserRequestDTO - данные отзыва
     * @return сохраненные данные
     */
    @Override
    public ReviewsUserResponseDTO addUserReview(UUID userReviewerId, ReviewsUserRequestDTO reviewsUserRequestDTO) {
        Review review = reviewsRepository.saveAndFlush(reviewsMapper.ratingUserRequestDtoToRating(
                userReviewerId,
                reviewsUserRequestDTO));
        return reviewsMapper.ratingToRatingUserResponseDto(review);
    }

    /**
     * Этот метод выводи список рейтингов по ID клиента
     *
     * @param userId - UUID id пользователя в базе данных
     * @return список отзывов по пользователю
     */
    @Override
    public Set<ReviewsUserResponseDTO> getUserReviews(UUID userId) {
        Set<Review> reviews = reviewsRepository.findByUser_Id(userId);
        return reviews.stream().map(reviewsMapper::ratingToRatingUserResponseDto).collect(Collectors.toSet());
    }

    /**
     * Добавление рейтинга квартире
     *
     * @param reviewsFlatRequestDTO - данные отзыва
     * @return сохраненные данные
     */
    @Override
    public ReviewsFlatResponseDTO addFlatReview(UUID userReviewerId, ReviewsFlatRequestDTO reviewsFlatRequestDTO) {
        Review review = reviewsRepository.saveAndFlush(reviewsMapper.ratingFlatRequestDtoToRating(
                userReviewerId,
                reviewsFlatRequestDTO));
        return reviewsMapper.ratingToRatingFlatResponseDto(review);
    }

    /**
     * Этот метод выводи список рейтингов по ID квартиры
     *
     * @param id - Long id квартиры в базе данных
     * @return список отзывов по квартире
     */
    @Override
    public Set<ReviewsFlatResponseDTO> getFlatReviews(UUID id) {
        Set<Review> reviews = reviewsRepository.findByFlat_Id(id);
        return reviews.stream().map(reviewsMapper::ratingToRatingFlatResponseDto).collect(Collectors.toSet());
    }
}