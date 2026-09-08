package com.ficsolution.roster.web.dto.role;

import com.ficsolution.roster.model.Role;

import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name
) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(role.getId(), role.getName());
    }
}
