package com.ficsolution.roster.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateShiftEmployeeRequest(
        @NotNull UUID employeeId
) {
}
