package org.example.sharing.rest.handler.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationError {

    private List<ValidationErrorValue> violations = new ArrayList<>();

}
