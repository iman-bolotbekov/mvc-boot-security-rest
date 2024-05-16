package ru.kata.spring.boot_security.demo.services;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationServiceImpl implements AuthorizationService {
    private final String ADMIN = "ADMIN";
    private final String USER = "USER";

    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getAuthorities() {
        return customizer -> {
            try {
                customizer
                        .requestMatchers("api/admin").hasRole(ADMIN)
                        .requestMatchers("api/users").hasRole(USER)
                        .anyRequest().permitAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
