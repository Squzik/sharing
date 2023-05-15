package org.example.sharing.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageListResponseDto {
    @Schema(description = "Id чат комнаты в базе данных")
    private UUID chatRoomId;

    @Builder.Default
    @Schema(description = "Список сообщений")
    private List<ChatMessageResponseDTO> messages = new ArrayList<>();
}
