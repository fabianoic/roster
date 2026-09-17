package com.ficsolution.roster.web.dto;

import com.ficsolution.roster.model.enumModel.EmployeeStatus;

import java.util.UUID;

public record EmployeeFilter(
        UUID roleId,
        String name,
        String email,
        EmployeeStatus status
) {
}
