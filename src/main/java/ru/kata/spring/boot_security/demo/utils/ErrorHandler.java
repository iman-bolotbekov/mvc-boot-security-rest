package ru.kata.spring.boot_security.demo.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Component
public class ErrorHandler {
    public String getErrorsString(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        fieldErrors.forEach(fieldError -> errorMsg.append(fieldError.getField())
                .append(" - ").append(fieldError
                        .getDefaultMessage())
                .append(";"));
        return errorMsg.toString();
    }
}
