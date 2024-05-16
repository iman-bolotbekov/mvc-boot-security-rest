package ru.kata.spring.boot_security.demo.controllers.api;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.AuthenticationDTO;
import ru.kata.spring.boot_security.demo.dto.AuthUserDTO;
import ru.kata.spring.boot_security.demo.dto.ResponseDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.JWTServiceImpl;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.services.ValidationService;
import ru.kata.spring.boot_security.demo.utils.UserValidator;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final UserValidator userValidator;
    private final RegistrationService registerService;
    private final JWTServiceImpl jwtServiceImpl;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final ValidationService validationService;
    public AuthRestController(UserValidator userValidator,
                              RegistrationService registerService,
                              JWTServiceImpl jwtServiceImpl,
                              ModelMapper modelMapper,
                              AuthenticationManager authenticationManager,
                              UserService userService,
                              ValidationService validationService) {
        this.userValidator = userValidator;
        this.registerService = registerService;
        this.jwtServiceImpl = jwtServiceImpl;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.validationService = validationService;
    }
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> performRegistration(@RequestBody @Valid AuthUserDTO authUserDTO,
                                                           BindingResult bindingResult) {
        User user = modelMapper.map(authUserDTO, User.class);
        this.userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ResponseDTO(validationService.getErrorsString(bindingResult)),
                                        HttpStatus.BAD_REQUEST);
        }
        this.registerService.register(user);
        return new ResponseEntity<>(new ResponseDTO(Map.of("jwt-token",
                                    this.jwtServiceImpl.generateToken(user.getUsername()))), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> performLogin(@RequestBody @Valid AuthenticationDTO authenticationDTO,
                                                    BindingResult bindingResult) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ResponseDTO(validationService.getErrorsString(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try {
            this.authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        User user = userService.findByUsername(authenticationDTO.getUsername()).orElse(null);
        return new ResponseEntity<>(new ResponseDTO(Map.of("jwtToken", this.jwtServiceImpl.generateToken(authenticationDTO.getUsername()),
                                                           "id", user.getId(),
                                                           "username", user.getUsername(),
                                                           "email", user.getEmail(),
                                                           "age", user.getAge(),
                                                           "roles", user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")))),
                                    HttpStatus.OK);
    }
}
