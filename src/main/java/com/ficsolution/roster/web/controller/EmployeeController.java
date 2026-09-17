package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.service.EmployeeService;
import com.ficsolution.roster.web.dto.employee.ChangePasswordRequest;
import com.ficsolution.roster.web.dto.employee.CreateEmployeeRequest;
import com.ficsolution.roster.web.dto.employee.EmployeeResponse;
import com.ficsolution.roster.web.dto.employee.UpdateEmployeeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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
    public ResponseEntity<List<EmployeeResponse>> retrieveAllEmployees() {
        List<EmployeeResponse> employeeResponses = employeeService.retrieveAllEmployees().stream().map(EmployeeResponse::from).toList();
        return ResponseEntity.ok(employeeResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployeeInfo(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        Employee employee = updateEmployeeRequest.toEntity();
        EmployeeResponse response = EmployeeResponse.from(employeeService.updateEmployeeInfo(id, employee));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/changestatus")
    public ResponseEntity<EmployeeResponse> changeEmployeeStatus(@PathVariable UUID id) {
        EmployeeResponse response = EmployeeResponse.from(employeeService.changeEmployeeStatus(id));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/changepassword")
    public ResponseEntity<EmployeeResponse> changeEmployeePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(EmployeeResponse.from(employeeService.changeEmployeePassword(id, changePasswordRequest)));
    }

}
