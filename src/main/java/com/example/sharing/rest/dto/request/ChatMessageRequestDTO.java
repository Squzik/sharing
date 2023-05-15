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
public class ChatMessageRequestDTO {

    @NotNull(message = "Ошибка! Вы не указали id чат комнаты")
    @Schema(description = "Id чат комнаты в базе данных")
    private UUID chatRoomId;

    @NotNull(message = "Ошибка! Вы не ввели сообщение")
    @Schema(description = "Сообщение")
    private String message;

}
