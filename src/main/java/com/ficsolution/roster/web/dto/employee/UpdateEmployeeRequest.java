package com.ficsolution.roster.web.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateEmployeeRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull UUID roleId
        ) {
}
