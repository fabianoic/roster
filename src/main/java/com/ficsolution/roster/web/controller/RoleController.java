package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.service.RoleService;
import com.ficsolution.roster.web.dto.role.RoleRequest;
import com.ficsolution.roster.web.dto.role.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        RoleResponse roleResponse = RoleResponse.from(roleService.createRole(roleRequest));
        return ResponseEntity.created(URI.create(String.format("/roles/%s", roleResponse.id()))).body(roleResponse);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> retrieveAllRoles() {
        List<RoleResponse> rolesResponse = roleService.retrieveAllRoles().stream().map(RoleResponse::from).toList();
        return ResponseEntity.ok(rolesResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> retrieveById(@PathVariable UUID id) {
        RoleResponse roleResponse = RoleResponse.from(roleService.retrieveById(id));
        return ResponseEntity.ok(roleResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRoleName(@PathVariable UUID id, @Valid @RequestBody RoleRequest roleRequest) {
        RoleResponse roleResponse = RoleResponse.from(roleService.updateRoleName(id, roleRequest));
        return ResponseEntity.ok(roleResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
