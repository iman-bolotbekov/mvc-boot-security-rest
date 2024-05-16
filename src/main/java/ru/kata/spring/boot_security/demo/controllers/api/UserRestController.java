package ru.kata.spring.boot_security.demo.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService service;
    public UserRestController(UserService service) {
        this.service = service;
    }
    @GetMapping
    public ResponseEntity<UserDTO> index(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = service.findByUsername(userDetails.getUsername());
        return optionalUser.map(user -> new ResponseEntity<>(service.findOne(user), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
}
