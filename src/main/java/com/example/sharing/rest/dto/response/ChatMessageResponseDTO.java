package org.example.sharing.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDTO {

    @Schema(description = "Id отправителя в базе данных")
    private UUID senderId;

    @Schema(description = "Сообщение")
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy hh:mm")
    @Schema(
            description = "Дата и время сообщения",
            type = "string",
            example = "01.01.2000 12:00"
    )
    private LocalDateTime dateTime;

}
