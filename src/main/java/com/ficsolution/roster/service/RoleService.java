package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.repository.PermissionRepository;
import com.ficsolution.roster.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public Role createRole(Role role) {
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
    public Role updateRoleName(UUID id, Role roleRequest) {
        Role retrievedRole = retrieveById(id);
        retrievedRole.setName(roleRequest.getName());
        return roleRepository.save(retrievedRole);
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = retrieveById(id);
        roleRepository.delete(role);
    }

    @Transactional(readOnly = true)
    public List<Permission> retrievePermissions(UUID roleId) {
        return sortedByName(retrieveById(roleId).getPermissions());
    }

    @Transactional
    public List<Permission> replacePermissions(UUID roleId, Set<String> permissionNames) {
        Role role = retrieveById(roleId);
        List<Permission> permissions = permissionRepository.findByNameIn(permissionNames);
        if (permissions.size() != permissionNames.size()) {
            Set<String> found = permissions.stream().map(Permission::getName).collect(Collectors.toSet());
            String missing = permissionNames.stream().filter(name -> !found.contains(name)).sorted()
                    .collect(Collectors.joining(", "));
            throw new ObjectNotFoundException("Permission", missing);
        }
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
        return sortedByName(roleRepository.save(role).getPermissions());
    }

    private static List<Permission> sortedByName(Collection<Permission> permissions) {
        return permissions.stream().sorted(Comparator.comparing(Permission::getName)).toList();
    }
}
