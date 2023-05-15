package org.example.sharing.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsUserResponseDTO {

    private UUID id;

    private LocalDateTime dateTime;

    private Double score;

    private String comment;

    private UUID userId;

    private UserListResponseDTO reviewerUser;
}