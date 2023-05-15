package org.example.sharing.rest.dto.request;

import org.example.sharing.utils.validation.PatternConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
public class UpdatePasswordRequestDTO {

    @Schema(
            description = "Новый пароль",
            example = "password2")
    @NotEmpty(message = "Ошибка! Вы не указали новый пароль")
    @Pattern(regexp = PatternConstants.PASSWORD,
            message = "Пароль должен быть:\n" +
                    "От 8 символов;\n" +
                    "Содержать только английские буквы;\n" +
                    "Содержать минимум одну цифру;\n")
    private String newPassword;

    @Schema(
            description = "Старый пароль",
            example = "password1")
    @NotEmpty(message = "Ошибка! Вы не указали старый пароль")
    @Pattern(regexp = PatternConstants.PASSWORD,
            message = "Пароль должен быть:\n" +
                    "От 8 символов;\n" +
                    "Содержать только английские буквы;\n" +
                    "Содержать минимум одну цифру;\n")
    private String oldPassword;
}
