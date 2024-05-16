package ru.kata.spring.boot_security.demo.services;

public interface JWTService {
    String generateToken(String username);
    String validateTokenAndRetrieveClaim(String token);
}
