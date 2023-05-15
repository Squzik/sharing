package org.example.sharing.rest.handler.exception;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class ValidationErrorValue {

    private final String fieldName;

    private final String message;

}
