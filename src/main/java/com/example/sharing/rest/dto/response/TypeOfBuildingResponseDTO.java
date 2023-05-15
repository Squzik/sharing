package org.example.sharing.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeOfBuildingResponseDTO {
    @Schema(description = "Первичный ключ")
    private UUID id;

    @Schema(description = "Наименование")
    private String title;

    @Schema(description = "Описание")
    private String description;
}
