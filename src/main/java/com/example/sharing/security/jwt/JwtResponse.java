package org.example.sharing.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {

    @Schema(description = "Токен доступа для работы с API")
    private String accessToken;

    @Schema(description = "Токен для обновления Access и Refresh токенов")
    private String refreshToken;

    @Schema(description = "Id пользователя в базе данных")
    private UUID id;
}