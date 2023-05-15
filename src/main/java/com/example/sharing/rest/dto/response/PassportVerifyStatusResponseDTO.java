package org.example.sharing.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PassportVerifyStatusResponseDTO {

    @Schema(description = "Id в базе данных")
    private UUID id;
    @Schema(description = "Название статуса")
    private String title;
    @Schema(description = "Описание статуса")
    private String description;
}
