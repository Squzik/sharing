package org.example.sharing.rest.handler;

import org.example.sharing.rest.handler.exception.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class AdviseController extends ResponseEntityExceptionHandler {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ErrorDto {
        private int code;
        private HttpStatus status;
        private String description;
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException exception) {
        return createResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            BadRequestException.class,
            SecurityException.class,
            FileException.class})
    public ResponseEntity<ErrorDto> handleBadRequestException(Exception exception) {
        return createResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnAuthorisedException.class)
    public ResponseEntity<ErrorDto> handleAuthException(UnAuthorisedException exception) {
        return createResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = PSQLException.class)
    public ResponseEntity<ErrorDto> handlePSQLException(PSQLException exception) {
        return createResponseEntity(exception.getMessage(), HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public @NotNull ResponseEntity<Object> handleConstraintValidationException(
            ConstraintViolationException e) {
        ValidationError error = new ValidationError();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            error.getViolations().add(
                    new ValidationErrorValue(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                        @NotNull HttpHeaders headers,
                                                                        @NotNull HttpStatus status,
                                                                        @NotNull WebRequest request) {
        ValidationError error = new ValidationError();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getViolations().add(
                    new ValidationErrorValue(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return new ResponseEntity<>(error, status);
    }


    private ResponseEntity<ErrorDto> createResponseEntity(String message, int code, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(
                        ErrorDto.builder()
                                .code(code)
                                .status(status)
                                .description(message)
                                .build()
                );
    }
}
