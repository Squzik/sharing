package org.example.sharing.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.sharing.rest.dto.s3.S3FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    @Schema(description = "Id в базе данных")
    private UUID id;

    @Schema(description = "Фамилия")
    private String surname;

    @Schema(description = "Имя")
    private String name;

    @Schema(description = "Отчество")
    private String patronymic;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Schema(
            description = "Дата рождения",
            type = "string",
            example = "01.01.2000"
    )
    private LocalDate dateOfBirth;

    @Schema(description = "Номер телефона")
    private String phone;

    @Schema(
            description = "Электронная почта",
            example = "Test@test.test"
    )
    private String mail;

    @Schema(description = "Фото профиля пользователя")
    private S3FileDto avatarFile;

    @Schema(description = "Роль пользователя")
    private String roleName;

    @Schema(description = "Статус подтверждения паспорта")
    private PassportVerifyStatusResponseDTO passportVerifyStatus;
}