package org.example.sharing.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackListRequestDto {

    @NotNull(message = "Ошибка! Вы не указали ID пользователя")
    @Schema(description = "ID пользователя")
    private UUID userId;

    @Schema(description = "Комментарий")
    private String commentary;
}
