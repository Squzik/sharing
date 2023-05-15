package org.example.sharing.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@Builder
public class JwtRequest {

    @Schema(
            description = "Логин",
            example = "Test@test.test"
    )
    @NotEmpty(message = "Ошибка! Вы не указали логин")
    private String login;

    @Schema(
            description = "Имя",
            example = "password1"
    )
    @NotEmpty(message = "Ошибка! Вы не указали пароль")
    @Pattern(regexp = "([a-zA-Z]+\\d+[a-zA-Z]*)|([a-zA-Z]*\\d+[a-zA-Z]+){8,}",
            message = "Пароль должен быть:\n" +
                    "От 8 символов;\n" +
                    "Содержать только английские буквы;\n" +
                    "Содержать минимум одну цифру;\n")
    private String password;
}
