package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.service.EmployeeService;
import com.ficsolution.roster.web.dto.employee.CreateEmployeeRequest;
import com.ficsolution.roster.web.dto.employee.EmployeeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest createEmployeeRequest) {
        Employee employee = employeeService.createEmployee(createEmployeeRequest);
        return ResponseEntity.created(URI.create(String.format("/employees/%s", employee.getId())))
                .body(EmployeeResponse.from(employee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> retrieveEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(EmployeeResponse.from(employeeService.retrieveEmployeeById(id)));
    }
}
