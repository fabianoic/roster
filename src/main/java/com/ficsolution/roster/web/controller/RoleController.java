package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.repository.specification.RoleSpecification;
import com.ficsolution.roster.service.RoleService;
import com.ficsolution.roster.web.dto.common.PagedResponse;
import com.ficsolution.roster.web.dto.permission.PermissionResponse;
import com.ficsolution.roster.web.dto.permission.RolePermissionsRequest;
import com.ficsolution.roster.web.dto.role.RoleFilter;
import com.ficsolution.roster.web.dto.role.RoleRequest;
import com.ficsolution.roster.web.dto.role.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        Role role = new Role(null, roleRequest.name());
        RoleResponse roleResponse = RoleResponse.from(roleService.createRole(role));
        return ResponseEntity.created(URI.create(String.format("/roles/%s", roleResponse.id()))).body(roleResponse);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<RoleResponse>> retrieveAllRoles(@ModelAttribute RoleFilter filter, @PageableDefault(sort = "name") Pageable pageable) {
        Specification<Role> specification = Specification.allOf(
                RoleSpecification.nameContains(filter.name())
        );
        Page<Role> roles = roleService.retrieveAllRoles(specification, pageable);
        return ResponseEntity.ok(PagedResponse.from(roles.map(RoleResponse::from)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> retrieveById(@PathVariable UUID id) {
        RoleResponse roleResponse = RoleResponse.from(roleService.retrieveById(id));
        return ResponseEntity.ok(roleResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRoleName(@PathVariable UUID id, @Valid @RequestBody RoleRequest roleRequest) {
        RoleResponse roleResponse = RoleResponse.from(roleService.updateRoleName(id, roleRequest.toEntity()));
        return ResponseEntity.ok(roleResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<PermissionResponse>> retrieveRolePermissions(@PathVariable UUID id) {
        List<PermissionResponse> responses = roleService.retrievePermissions(id).stream()
                .map(PermissionResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<List<PermissionResponse>> replaceRolePermissions(@PathVariable UUID id, @Valid @RequestBody RolePermissionsRequest request) {
        List<PermissionResponse> responses = roleService.replacePermissions(id, request.permissions()).stream()
                .map(PermissionResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
