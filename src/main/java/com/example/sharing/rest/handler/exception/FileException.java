package org.example.sharing.rest.handler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class FileException extends RuntimeException {

    public FileException(String message) {
        super(message);
    }

    public static final String FILE_IS_BROKEN = "Некорректный файл";

    public static final String FILE_IS_NOT_UPLOAD = "Ошибка загрузки файла, повторите попытку позже";

    public static final String INCORRECT_FILE_ID = "Некорректный идентификатор файла, введен {1}";
}
