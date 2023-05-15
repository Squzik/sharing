package org.example.sharing.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.sharing.utils.validation.OnUpdate;
import org.example.sharing.utils.validation.PatternConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Входное dto юзера")
public class UserRequestDTO extends MailRequestDto {

    @NotBlank(message = "Фамилия не может быть пустой")
    @Schema(description = "Фамилия")
    private String surname;

    @NotBlank(message = "Имя не может быть пустым")
    @Schema(description = "Имя")
    private String name;

    @NotBlank(message = "Отчество не может быть пустым")
    @Schema(description = "Отчество")
    private String patronymic;

    @NotNull(message = "Ошибка! Вы не указали дату рождения")
    @Schema(description = "Дата рождения",
            type = "string",
            example = "01.01.2000")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    @Pattern(regexp = PatternConstants.PASSWORD,
            groups = {OnUpdate.class, Default.class},
            message = "Пароль должен быть:\n" +
                    "От 8 символов;\n" +
                    "Содержать только английские буквы;\n" +
                    "Содержать минимум одну цифру;\n")
    @NotNull(message = "Ошибка! Вы не указали пароль")
    @Schema(description = "Пароль",
            example = "password1")
    private String password;
}
