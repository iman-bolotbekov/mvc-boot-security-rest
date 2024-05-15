package ru.kata.spring.boot_security.demo.controllers.api;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
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
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.utils.ErrorHandler;
import ru.kata.spring.boot_security.demo.utils.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserValidator userValidator;
    private final UserService userService;
    private final RoleService roleService;
    private final ErrorHandler errorHandler;
    public AdminRestController(UserService userService,
                               UserValidator userValidator,
                               RoleService roleService,
                               ErrorHandler errorHandler) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.roleService = roleService;
        this.errorHandler = errorHandler;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();
        Hibernate.initialize(user.getRoles());
        List<UserDTO> users = new ArrayList<>();
        userService.findAll().forEach(u -> users.add(userService.convertToUserDTO(u)));
        return new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.convertToUserDTO(user),
                                                           "users", users)),
                                    HttpStatus.OK);
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable("id") int id) {
        Optional<User> optionalUser = userService.findOne(id);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();
        Hibernate.initialize(user.getRoles());
        return new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.convertToUserDTO(user))),
                                    HttpStatus.OK);
    }
    @PostMapping("/users")
    public ResponseEntity<ResponseDTO> addUser(@RequestBody @Valid AddUserDTO userDTO,
                                               BindingResult bindingResult) {
        User user = userService.convertToUserFromAddUserDTO(userDTO);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ResponseDTO(errorHandler.getErrorsString(bindingResult)),
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
            return new ResponseEntity<>(new ResponseDTO(errorHandler.getErrorsString(bindingResult)),
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
