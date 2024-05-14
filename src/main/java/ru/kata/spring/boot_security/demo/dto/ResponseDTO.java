package ru.kata.spring.boot_security.demo.dto;

import jakarta.annotation.Nullable;

import java.util.Map;

public class ResponseDTO {
    @Nullable
    private Map<String, Object> data;
    @Nullable
    private String message;
    public ResponseDTO() {}
    public ResponseDTO(Map<String, Object> data) {
        this.data = data;
    }
    public ResponseDTO(String message) {
        this.message = message;
    }
    public ResponseDTO(Map<String, Object> data, String message) {
        this.data = data;
        this.message = message;
    }

    @Nullable
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(@Nullable Map<String, Object> data) {
        this.data = data;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }
}
