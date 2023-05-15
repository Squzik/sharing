package org.example.sharing.rest.dto.request;

import org.example.sharing.utils.validation.PatternConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Входное dto восстановления пароля")
public class PasswordRecoveryRequestDto {

    @Pattern(regexp = PatternConstants.PASSWORD,
            message = "Пароль должен быть:\n" +
                    "От 8 символов;\n" +
                    "Содержать только английские буквы;\n" +
                    "Содержать минимум одну цифру;\n")
    @NotNull(message = "Ошибка! Вы не указали пароль")
    @Schema(description = "Пароль",
            example = "password1")
    private String password;
}
