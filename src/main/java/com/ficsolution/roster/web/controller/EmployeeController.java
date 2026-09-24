package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.service.EmployeeService;
import com.ficsolution.roster.specification.EmployeeSpecification;
import com.ficsolution.roster.web.dto.EmployeeFilter;
import com.ficsolution.roster.web.dto.employee.ChangePasswordRequest;
import com.ficsolution.roster.web.dto.employee.CreateEmployeeRequest;
import com.ficsolution.roster.web.dto.employee.EmployeeResponse;
import com.ficsolution.roster.web.dto.employee.UpdateEmployeeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @GetMapping(params = "email")
    public ResponseEntity<EmployeeResponse> retrieveEmployeeByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(EmployeeResponse.from(employeeService.retrieveEmployeeByEmail(email)));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> retrieveAllEmployees(@ModelAttribute EmployeeFilter filter, Pageable pageable) {
        Specification<Employee> spec = Specification.allOf(
                EmployeeSpecification.hasRoleId(filter.roleId()),
                EmployeeSpecification.hasName(filter.name()),
                EmployeeSpecification.hasEmail(filter.email()),
                EmployeeSpecification.hasStatus(filter.status())
        );
        Page<Employee> employees = employeeService.retrieveAllEmployees(spec, pageable);
        Page<EmployeeResponse> responses = employees.map(EmployeeResponse::from);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployeeInfo(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        Employee employee = updateEmployeeRequest.toEntity();
        EmployeeResponse response = EmployeeResponse.from(employeeService.updateEmployeeInfo(id, employee));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/change-status")
    public ResponseEntity<EmployeeResponse> changeEmployeeStatus(@PathVariable UUID id) {
        EmployeeResponse response = EmployeeResponse.from(employeeService.changeEmployeeStatus(id));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<EmployeeResponse> changeEmployeePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(EmployeeResponse.from(employeeService.changeEmployeePassword(id, changePasswordRequest)));
    }

}
