package com.ficsolution.roster.web.dto.employee;

import com.ficsolution.roster.model.enums.EmployeeStatus;

import java.util.UUID;

public record EmployeeFilter(
        UUID roleId,
        String name,
        String email,
        EmployeeStatus status
) {
}
