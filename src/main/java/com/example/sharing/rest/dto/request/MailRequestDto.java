package org.example.sharing.rest.dto.request;

import org.example.sharing.utils.validation.PatternConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Входное дто для почты пользователя")
public class MailRequestDto {

    @Schema(description = "Почта", example = "Test@test.test")
    @NotNull(message = "Ошибка! Вы не указали email")
    @Pattern(regexp = PatternConstants.MAIL, message = "Вы ввели некорректный email")
    private String mail;
}
