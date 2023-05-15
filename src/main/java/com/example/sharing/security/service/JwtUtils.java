package org.example.sharing.security.service;

import org.example.sharing.rest.handler.exception.UnAuthorisedException;
import org.example.sharing.security.jwt.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

import static org.example.sharing.rest.handler.exception.UnAuthorisedException.JWT_TOKEN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setMail(claims.get("sub", String.class));
        String id = claims.get("id", String.class);
        if (Objects.isNull(id)) {
            throw new UnAuthorisedException(JWT_TOKEN);
        }
        jwtInfoToken.setId(UUID.fromString(id));
        return jwtInfoToken;
    }

}
