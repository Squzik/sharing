package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.ReviewsFlatRequestDTO;
import org.example.sharing.rest.dto.request.ReviewsUserRequestDTO;
import org.example.sharing.rest.dto.response.ReviewsFlatResponseDTO;
import org.example.sharing.rest.dto.response.ReviewsUserResponseDTO;
import org.example.sharing.service.ReviewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;
import static org.example.sharing.utils.ServiceUtils.getUserIdFromCurrentSession;

/**
 * @author Squzik
 * @since 28.04.2022
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@Tag(name = "ReviewsController", description = "Контроллер отзывов")
public class ReviewsController {

    private final ReviewsService reviewsService;

    @SecurityRequirement(name = SCHEME_NAME)
    @PostMapping("/user")
    @Operation(summary = "Добавление отзыва на пользователя")
    public ResponseEntity<ReviewsUserResponseDTO> addUserReview(@Valid @RequestBody ReviewsUserRequestDTO reviewsUserRequestDTO) {
        ReviewsUserResponseDTO reviewsUserResponseDTO = reviewsService.addUserReview(
                getUserIdFromCurrentSession(),
                reviewsUserRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/reviews/user")
                .buildAndExpand(reviewsUserResponseDTO.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(reviewsUserResponseDTO);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Вывод всех отзывов по ID пользователя")
    public ResponseEntity<Set<ReviewsUserResponseDTO>> getUserReviews(@PathVariable UUID userId) {
        return ResponseEntity.ok(reviewsService.getUserReviews(userId));
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @PostMapping("/flat")
    @Operation(summary = "Добавление отзыва на квартиру")
    public ResponseEntity<ReviewsFlatResponseDTO> addFlatReview(@Valid @RequestBody ReviewsFlatRequestDTO reviewsFlatRequestDTO) {
        ReviewsFlatResponseDTO reviewsFlatResponseDTO = reviewsService.addFlatReview(
                getUserIdFromCurrentSession(),
                reviewsFlatRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/reviews/flat")
                .buildAndExpand(reviewsFlatResponseDTO.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(reviewsFlatResponseDTO);
    }

    @GetMapping("/flat/{flatId}")
    @Operation(summary = "Вывод всех отзывов по ID квартиры")
    public ResponseEntity<Set<ReviewsFlatResponseDTO>> getFlatReviews(@PathVariable UUID flatId) {
        return ResponseEntity.ok(reviewsService.getFlatReviews(flatId));
    }
}