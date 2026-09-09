package com.ficsolution.roster.web.dto.employee;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.web.dto.role.RoleResponse;

import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String name,
        String email,
        RoleResponse role,
        EmployeeStatus status
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                RoleResponse.from(employee.getRole()),
                employee.getStatus()
        );
    }
}
