package ru.kata.spring.boot_security.demo.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    public RoleServiceImpl(RoleRepository roleRepository,
                           ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }
    public List<RoleDTO> findAll() {
        List<RoleDTO> roles = new ArrayList<>();
        roleRepository.findAll().forEach(role -> roles.add(convertToRoleDTO(role)));
        return roles;
    }
    public Role findOne(int id) {
        return roleRepository.findById(id).orElse(null);
    }
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
    public Role save(Role role) {
        return roleRepository.save(role);
    }
    public void update(int id, Role updatedRole) {
        updatedRole.setId(id);
        roleRepository.save(updatedRole);
    }
    public void delete(int id) {
        roleRepository.deleteById(id);
    }
    public Role convertToRole(RoleDTO roleDTO) {
        return modelMapper.map(roleDTO, Role.class);
    }
    public RoleDTO convertToRoleDTO(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }
}
