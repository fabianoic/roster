package com.ficsolution.roster.web.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RolePermissionsRequest(
        @NotNull Set<@NotBlank String> permissions
) {
}
