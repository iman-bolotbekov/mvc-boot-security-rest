package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.dto.AddUserDTO;
import ru.kata.spring.boot_security.demo.dto.AuthUserDTO;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

public abstract class UserService implements UserDetailsService {
    public abstract Optional<User> findByUsername(String username);
    public abstract List<User> findAll();
    public abstract Optional<User> findOne(int id);
    public abstract void save(User user);
    public abstract void update(int id, User updatedPerson);
    public abstract void delete(int id);
    public abstract AuthUserDTO convertToAuthUserDTO(User user);
    public abstract UserDTO convertToUserDTO(User user);
    public abstract User convertToUserFromAuthUserDTO(AuthUserDTO authUserDTO);
    public abstract User convertToUserFromUserDTO(UserDTO userDTO);
    public abstract User convertToUserFromAddUserDTO(AddUserDTO addUserDTO);
}
