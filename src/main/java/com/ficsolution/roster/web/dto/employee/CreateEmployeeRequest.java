package com.ficsolution.roster.web.dto.employee;

import com.ficsolution.roster.annotation.ValidPassword;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateEmployeeRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email String email,
        @NotBlank @ValidPassword String password,
        @NotNull UUID roleId
) {
    public Employee toEntity() {
        return Employee.builder().name(name).email(email).password(password).role(Role.builder().id(roleId).build()).build();
    }
}
