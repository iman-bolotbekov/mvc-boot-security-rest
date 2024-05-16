package ru.kata.spring.boot_security.demo.controllers.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.services.ValidationService;
import ru.kata.spring.boot_security.demo.utils.UserValidator;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserValidator userValidator;
    private final UserService userService;
    private final RoleService roleService;
    private final ValidationService validationService;
    public AdminRestController(UserService userService,
                               UserValidator userValidator,
                               RoleService roleService,
                               ValidationService validationService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.roleService = roleService;
        this.validationService = validationService;
    }
    @GetMapping("/users/roles")
    public ResponseEntity<ResponseDTO> getUserRoles() {
        return new ResponseEntity<>(new ResponseDTO(Map.of("roles", roleService.findAll())), HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<ResponseDTO> getUsers() {
        return new ResponseEntity<>(new ResponseDTO(Map.of("users", userService.findAll())), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<ResponseDTO> index(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        return optionalUser.map(user -> new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.findOne(user),
                "users", userService.findAll())),
                HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable("id") int id) {
        Optional<User> optionalUser = userService.findOne(id);
        return optionalUser.map(user -> new ResponseEntity<>(new ResponseDTO(Map.of("user", userService.findOne(user))),
                HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
    @PostMapping("/users")
    public ResponseEntity<ResponseDTO> addUser(@RequestBody @Valid AddUserDTO userDTO,
                                               BindingResult bindingResult) {
        User user = userService.convertToUserFromAddUserDTO(userDTO);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ResponseDTO(validationService.getErrorsString(bindingResult)),
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
            return new ResponseEntity<>(new ResponseDTO(validationService.getErrorsString(bindingResult)),
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
