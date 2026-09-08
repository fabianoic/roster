package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.repository.RoleRepository;
import com.ficsolution.roster.web.dto.role.RoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(RoleRequest roleRequest) {
        Role role = new Role(null, roleRequest.name());
        return roleRepository.save(role);
    }

    public List<Role> retrieveAllRoles() {
        return roleRepository.findAll();
    }

    public Role retrieveById(UUID id) {
        return roleRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Role", id.toString()));
    }

    public Role updateRoleName(UUID id, RoleRequest roleRequest) {
        Role role = retrieveById(id);
        role.setName(roleRequest.name());
        return roleRepository.save(role);
    }

    public void deleteRole(UUID id) {
        Role role = retrieveById(id);
        roleRepository.delete(role);
    }
}
