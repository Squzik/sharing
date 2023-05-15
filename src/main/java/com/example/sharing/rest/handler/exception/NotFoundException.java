package org.example.sharing.rest.handler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static final String BOOKING_FLAT_NOT_FOUND = "Данной забронированной квартиры не найдено";

    public static final String BOOKING_NOT_FOUND = "Данной аренды не найдено";

    public static final String CLIENT_NOT_FOUND = "Клиент не найден";

    public static final String PHOTO_NOT_FOUND = "Фото не найдено";

    public static final String FLAT_NOT_FOUND = "Квартира не найдена";

    public static final String SCIENER_USER_NOT_FOUND = "Аккаунт Sciener не найден";

    public static final String SCIENER_LOCK_NOT_FOUND = "Данного замка не существует";

    public static final String SCIENER_LOCK_NOT_FOUND_IN_API = "Данного замка не существует на Sciener аккаунте пользователя";

    public static final String SCIENER_NULL_GATEWAY = "Не удалось удалить ключ, т.к. замок не подключин к интернету";

    public static final String CLIENT_WITH_CURRENT_EMAIL_NOT_FOUND = "Пользователь с данным Email не найден";

    public static final String BOOKING_STATUS_NOT_FOUND = "Статус аренды %s не найден";

    public static final String MAIL_CONFIRMATION_NOT_FOUND = "Данные об активации данной электронной почты не найдены";

}
