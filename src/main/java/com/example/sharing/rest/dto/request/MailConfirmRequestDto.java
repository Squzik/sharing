package org.example.sharing.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailConfirmRequestDto extends MailRequestDto {
    @NotNull
    @Schema(description = "Код", example = "1234", type = "number")
    private Integer code;
}
