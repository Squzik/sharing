package org.example.sharing.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackListUserResponseDto {

    @Schema(description = "Дто пользователя")
    private UserListWithoutAvatarResponseDto user;

    @Schema(description = "Дата добавления",
            type = "string",
            example = "01.05.2023")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate dateOfCreate;

    @Schema(description = "Комментарий")
    private String commentary;
}
