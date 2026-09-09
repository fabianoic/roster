package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.repository.RoleRepository;
import com.ficsolution.roster.web.dto.role.RoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public Role createRole(RoleRequest roleRequest) {
        Role role = new Role(null, roleRequest.name());
        return roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public List<Role> retrieveAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role retrieveById(UUID id) {
        return roleRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Role", id.toString()));
    }

    @Transactional
    public Role updateRoleName(UUID id, RoleRequest roleRequest) {
        Role role = retrieveById(id);
        role.setName(roleRequest.name());
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = retrieveById(id);
        roleRepository.delete(role);
    }
}
