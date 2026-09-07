package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> retrieveAllRoles() {
        return roleRepository.findAll();
    }

    public Role retrieveById(UUID id) {
        return roleRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Role", id));
    }

    public Role updateRoleName(UUID id, String newRoleName) {
        Role role = retrieveById(id);
        role.setName(newRoleName);
        return roleRepository.save(role);
    }

    public void deleteRole(UUID id) {
        Role role = retrieveById(id);
        roleRepository.delete(role);
    }
}
