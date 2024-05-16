package ru.kata.spring.boot_security.demo.services;

import org.springframework.validation.BindingResult;

public interface ValidationService {
    String getErrorsString(BindingResult bindingResult);
}
