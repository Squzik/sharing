package org.example.sharing.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsUserRequestDTO {

    @NotNull(message = "Ошибка! Рейтинг не указан")
    @Schema(description = "Баллы рейтинга")
    private Double score;

    @Schema(description = "Комментарий")
    private String comment;

    @NotNull(message = "Ошибка! Клиент не указан")
    @Schema(description = "ID пользователя")
    private UUID userId;

}