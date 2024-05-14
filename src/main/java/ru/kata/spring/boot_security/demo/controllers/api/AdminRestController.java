package ru.kata.spring.boot_security.demo.controllers.api;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.kata.spring.boot_security.demo.dto.AddUserDTO;
import ru.kata.spring.boot_security.demo.dto.ResponseDTO;
import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.services.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.services.UserDetailsServiceImpl;
import ru.kata.spring.boot_security.demo.utils.UserValidator;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserValidator userValidator;
    private final UserDetailsServiceImpl userService;
    private final RoleServiceImpl roleService;
    public AdminRestController(UserDetailsServiceImpl userService,
                               UserValidator userValidator,
                               RoleServiceImpl roleService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.roleService = roleService;
    }
    @GetMapping("/users/roles")
    public ResponseEntity<ResponseDTO> getUserRoles() {
        List<RoleDTO> roles = new ArrayList<>();
        roleService.findAll().forEach(role -> roles.add(roleService.convertToRoleDTO(role)));
        return new ResponseEntity<>(new ResponseDTO(Map.of("roles", roles)), HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<ResponseDTO> getUsers() {
        List<UserDTO> users = new ArrayList<>();
        userService.findAll().forEach(u -> users.add(userService.convertToUserDTO(u)));
        return new ResponseEntity<>(new ResponseDTO(Map.of("users", users)), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<ResponseDTO> index() {
        ResponseEntity response;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Hibernate.initialize(user.getRoles());
            List<UserDTO> users = new ArrayList<>();
            userService.findAll().forEach(u -> users.add(userService.convertToUserDTO(u)));
            response = new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.convertToUserDTO(user),
                                                                   "users", users)), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable("id") int id) {
        ResponseEntity response;
        Optional<User> optionalUser = userService.findOne(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Hibernate.initialize(user.getRoles());
            response = new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.convertToUserDTO(user))),
                    HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
    @PostMapping("/users/create")
    public ResponseEntity<ResponseDTO> addUser(@RequestBody @Valid AddUserDTO userDTO,
                                   BindingResult bindingResult) {
        User user = userService.convertToUserFromAddUserDTO(userDTO);
        userValidator.validate(user, bindingResult);
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
        userService.save(user);
        return new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.convertToUserDTO(user))),
                HttpStatus.CREATED);
    }
    @PatchMapping("/users/{id}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable("id") int id,
                                   @RequestBody @Valid UserDTO userDTO,
                                   BindingResult bindingResult) {
        User user = userService.convertToUserFromUserDTO(userDTO);
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
        userService.update(id, user);
        return new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.convertToUserDTO(user))),
                HttpStatus.OK);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
