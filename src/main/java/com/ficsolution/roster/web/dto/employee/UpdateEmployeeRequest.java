package com.ficsolution.roster.web.dto.employee;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateEmployeeRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull UUID roleId
) {
    public Employee toEntity() {
        return Employee.builder()
                .name(name)
                .email(email)
                .role(Role.builder().id(roleId).build())
                .build();
    }
}
