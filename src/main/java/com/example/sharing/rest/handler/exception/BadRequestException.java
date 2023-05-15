package org.example.sharing.rest.handler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public static final String USER_MISMATCH = "Несоответствие авторизированного пользователя";

    public static final String CHAT_ROOM_CREATE = "Невозможно создать комнату";

    public static final String SCIENER_CODE_ALREADY_EXISTS = "Такой ключ уже существует";

    public static final String SCIENER_INVALID_VALUE = "Неподходящее значение для запроса";

    public static final String SCIENER_UNHANDLED_ERROR = "Произошла ошибка в работе с Sciener API";

    public static final String SCIENER_LOCK_EXISTS = "На аккаунте не существует данного замка";

    public static final String SCIENER_LOCK_LINKED = "Данный замок привязан к аккаунту";

    public static final String FLAT_HAVE_ACTIVE_BOOKING = "Квартира имеет активные бронирования";

    public static final String FLAT_INACTIVE = "Квартира имеет неактивный статус";

    public static final String WRONG_EMAIL = "Неподдерживаемая почта";

    public static final String ACCESS_TOKEN_ID_DOESNT_MATCH = "Id клиента не совпадает с id в запросе";

    public static final String EQUALS_NEW_AND_OLD_PASSWORD = "Старый и новый пароль совпадают";

    public static final String WRONG_OLD_PASSWORD = "Старый пароль введен неверно";

    public static final String WRONG_BOOKING_DATES = "Даты аренды не могут быть раньше чем текущее время";

    public static final String BOOKING_WRONG_STATUS = "Неправильный статус";

    public static final String BOOKING_EDITED_STATUS = "Статус аренды уже был изменен";

    public static final String BOOKING_NOT_APPROVED = "Аренда не была подтверждена";

    public static final String BOOKING_EXPIRED = "Аренда не активна всвязи с указанными сроками";

    public static final String BOOKING_WAS_APPROVE = "Удаление аренды незовможно, потому-что аренда в данный момент имеет активный статус";

    public static final String DATE_OF_BIRTH_IS_GREATER_THAN_CURRENT_TIME = "Дата рождения больше текущего времени";

    public static final String MAIL_IS_CONFIRMED = "Почта данного аккаунта уже подтверждена";

    public static final String CODE_IS_STILL_ACTIVE = "Нынешний код подтверждения еще активен";

    public static final String CODE_IS_NOT_ACTIVE = "Данный код не активен";

    public static final String CODE_NOT_EQUALS = "Код не подходит";
}
