package ru.kata.spring.boot_security.demo.services;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import ru.kata.spring.boot_security.demo.dto.AddUserDTO;
import ru.kata.spring.boot_security.demo.dto.AuthUserDTO;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl extends UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    public UserDetailsServiceImpl(UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  ModelMapper modelMapper,
                                  @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }
        Hibernate.initialize(user.get().getRoles());
        return new UserDetailsImpl(user.get());
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<UserDTO> findAll() {
        List<UserDTO> users = new ArrayList<>();
        userRepository.findAll().forEach(u -> users.add(convertToUserDTO(u)));
        return users;
    }
    public Optional<User> findOne(int id) {
        return userRepository.findById(id);
    }
    public UserDTO findOne(User user) {
        Hibernate.initialize(user.getRoles());
        return convertToUserDTO(user);
    }
    @Transactional
    public void save(User user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAge(user.getAge());
        user.setEmail(user.getEmail());
        Set<Role> roles = new HashSet<>();
        for (Role role : user.getRoles()) {
            roleRepository.findByName(role.getName()).ifPresent(roles::add);
        }
        user.setRoles(roles);
        userRepository.save(user);
    }
    @Transactional
    public void update(int id, User updatedUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return;
        }
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAge(updatedUser.getAge());
        Set<Role> updatedRoles = new HashSet<>();
        for (Role role : updatedUser.getRoles()) {
            roleRepository.findByName(role.getName()).ifPresent(updatedRoles::add);
        }
        existingUser.setRoles(updatedRoles);
        userRepository.save(existingUser);
    }
    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    public AuthUserDTO convertToAuthUserDTO(User user) {
        return modelMapper.map(user, AuthUserDTO.class);
    }
    public UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
    public User convertToUserFromAuthUserDTO(AuthUserDTO authUserDTO) {
        return modelMapper.map(authUserDTO, User.class);
    }
    public User convertToUserFromUserDTO(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
    public User convertToUserFromAddUserDTO(AddUserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
