package org.example.sharing.rest.handler.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnAuthorisedException extends RuntimeException {
    public UnAuthorisedException(String message) {
        super(message);
    }

    public static final String OLDPASSWORD_EQUALS_NEWPASSWORD = "Старый пароль совпадает с новым паролем.";

    public static final String WRONG_OLDPASSWORD = "Старый пароль введен неверно.";

    public static final String INCORRECT_PASSWORD = "Неправильный пароль";

    public static final String JWT_TOKEN = "Невалидный JWT токен";

    public static final String INCORRECT_VALUES = "Неправильный логин и'\'или пароль";

    public static final String SCIENER_JWT_TOKEN = "Невалидный JWT токен Sciener";

    public static final String MAIL_IS_NOT_CONFIRM = "Почта не подтверждена";

}
