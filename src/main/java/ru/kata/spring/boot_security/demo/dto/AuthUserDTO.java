package ru.kata.spring.boot_security.demo.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public class AuthUserDTO {
    @NotEmpty(message = "Имя пользователя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя пользователя должно быть от 2 до 100 символов длиной")
    private String username;
    private String password;
    @Nullable
    private String email;
    @Nullable
    private Integer age;

    public @NotEmpty(message = "Имя пользователя не должно быть пустым") @Size(min = 2, max = 100, message = "Имя пользователя должно быть от 2 до 100 символов длиной") String getUsername() {
        return username;
    }
    public void setUsername(@NotEmpty(message = "Имя пользователя не должно быть пустым") @Size(min = 2, max = 100, message = "Имя пользователя должно быть от 2 до 100 символов длиной") String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public Integer getAge() {
        return age;
    }

    public void setAge(@Nullable Integer age) {
        this.age = age;
    }
}
