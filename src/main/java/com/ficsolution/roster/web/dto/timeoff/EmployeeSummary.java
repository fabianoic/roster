package com.ficsolution.roster.web.dto.timeoff;

import com.ficsolution.roster.model.Employee;

import java.util.UUID;

public record EmployeeSummary(
        UUID id,
        String name
) {
    public static EmployeeSummary from(Employee employee) {
        return new EmployeeSummary(employee.getId(), employee.getName());
    }
}
