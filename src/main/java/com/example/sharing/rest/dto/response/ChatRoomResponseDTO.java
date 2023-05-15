package org.example.sharing.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDTO {

    @Schema(description = "Id чат комнаты в базе данных")
    private UUID id;

    @Schema(description = "Первый пользователь")
    private UserListResponseDTO firstUser;

    @Schema(description = "Второй пользователь")
    private UserListResponseDTO secondUser;
}
