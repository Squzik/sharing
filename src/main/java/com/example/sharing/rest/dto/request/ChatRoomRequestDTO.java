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
public class ChatRoomRequestDTO {

    @NotNull(message = "Ошибка! Вы не указали id первого пользователя")
    @Schema(description = "Id первого пользователя в базе данных")
    private UUID firstUserId;

    @NotNull(message = "Ошибка! Вы не указали id второго пользователя")
    @Schema(description = "Id второго пользователя в базе данных")
    private UUID secondUserId;

}
