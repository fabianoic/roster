package com.ficsolution.roster.web.dto.role;

import com.ficsolution.roster.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @NotBlank @Size(max = 60) String name
) {
    public Role toEntity() {
        return new Role(null, this.name);
    }
}
