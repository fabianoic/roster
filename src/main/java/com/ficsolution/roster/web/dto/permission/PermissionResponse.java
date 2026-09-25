package com.ficsolution.roster.web.dto.permission;

import com.ficsolution.roster.model.Permission;

import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String name,
        String description
) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(permission.getId(), permission.getName(), permission.getDescription());
    }
}
