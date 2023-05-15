package org.example.sharing.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsFlatResponseDTO {

    private UUID id;

    private LocalDateTime dateTime;

    private Double score;

    private String comment;

    private UUID flatId;

    private UserListResponseDTO reviewerUser;
}