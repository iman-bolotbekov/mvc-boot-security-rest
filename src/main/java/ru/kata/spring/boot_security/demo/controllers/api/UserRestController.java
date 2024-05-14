package ru.kata.spring.boot_security.demo.controllers.api;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.AuthUserDTO;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.services.UserDetailsServiceImpl;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserDetailsServiceImpl service;
    public UserRestController(UserDetailsServiceImpl service) {
        this.service = service;
    }
    @GetMapping
    public ResponseEntity<AuthUserDTO> index() {
        ResponseEntity response;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> optionalUser = service.findByUsername(userDetails.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Hibernate.initialize(user.getRoles());
            response = new ResponseEntity<>(service.convertToUserDTO(user), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
}
