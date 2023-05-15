package org.example.sharing.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class RefreshJwtRequest {

    @Schema(description = "Refresh токен для получения новых Access и Refresh")
    @NotEmpty
    public String refreshToken;
}
