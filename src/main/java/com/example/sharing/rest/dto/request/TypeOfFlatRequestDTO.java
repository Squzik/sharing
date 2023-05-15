package org.example.sharing.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeOfFlatRequestDTO {
    @NotNull(message = "Не указано наименование!")
    @Schema(description = "Наименование")
    private String title;

    @Schema(description = "Описание")
    private String description;
}