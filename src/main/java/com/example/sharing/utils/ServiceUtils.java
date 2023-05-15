package org.example.sharing.utils;

import org.example.sharing.rest.handler.exception.BadRequestException;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

@UtilityClass
public class ServiceUtils {

    public static void equalsClientIdWithCurrentSession(UUID id) {
        if (!id.equals(getUserIdFromCurrentSession())) {
            throw new BadRequestException(BadRequestException.ACCESS_TOKEN_ID_DOESNT_MATCH);
        }
    }

    @Nullable
    public static UUID getUserIdFromCurrentSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().equals("anonymousUser")
                ? null
                : UUID.fromString(authentication.getPrincipal().toString()); //NPE быть не может, так как проверка идет при парсинге токена
    }

    public static boolean checkNulls(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Генерация кода
     *
     * @param min - минимальная граница
     * @param max - максимальная граница
     * @return целочисленное число
     */
    public static Integer genCode(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
