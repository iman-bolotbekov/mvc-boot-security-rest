package ru.kata.spring.boot_security.demo.controllers.api;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.AuthenticationDTO;
import ru.kata.spring.boot_security.demo.dto.AuthUserDTO;
import ru.kata.spring.boot_security.demo.dto.ResponseDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.security.JWTUtil;
import ru.kata.spring.boot_security.demo.services.RegistrationServiceImpl;
import ru.kata.spring.boot_security.demo.services.UserDetailsServiceImpl;
import ru.kata.spring.boot_security.demo.utils.UserValidator;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final UserValidator userValidator;
    private final RegistrationServiceImpl registrationServiceImpl;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userService;
    public AuthRestController(UserValidator userValidator,
                              RegistrationServiceImpl registrationServiceImpl,
                              JWTUtil jwtUtil,
                              ModelMapper modelMapper,
                              AuthenticationManager authenticationManager,
                              UserDetailsServiceImpl userService) {
        this.userValidator = userValidator;
        this.registrationServiceImpl = registrationServiceImpl;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }
    @PostMapping("/register")
    public Map<String, String> performRegistration(@RequestBody @Valid AuthUserDTO authUserDTO,
                                                   BindingResult bindingResult) {
        User user = modelMapper.map(authUserDTO, User.class);
        this.userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return Map.of("message", "Error");
        }
        this.registrationServiceImpl.register(user);
        String token = this.jwtUtil.generateToken(user.getUsername());
        return Map.of("jwt-token", token);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> performLogin(@RequestBody @Valid AuthenticationDTO authenticationDTO,
                                                    BindingResult bindingResult) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            fieldErrors.forEach(fieldError -> errorMsg.append(fieldError.getField())
                    .append(" - ").append(fieldError
                            .getDefaultMessage())
                    .append(";"));
            return new ResponseEntity<>(new ResponseDTO(errorMsg.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            this.authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        String token = this.jwtUtil.generateToken(authenticationDTO.getUsername());
        Optional<User> optionalUser = userService.findByUsername(authenticationDTO.getUsername());
        User user = new User();
        if (optionalUser.isPresent()) {
             user = optionalUser.get();
        }
        return new ResponseEntity<>(new ResponseDTO(Map.of("jwtToken", token,
                                                            "id", user.getId(),
                                                            "username", user.getUsername(),
                                                            "email", user.getEmail(),
                                                            "age", user.getAge(),
                                                            "roles", user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")))),
                                    HttpStatus.OK);
    }
}
