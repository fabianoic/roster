package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.EmployeeNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> retrieveAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee retrieveEmployeeById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException("Employee not found, id: " + id));
    }

    public Employee updateEmployee(UUID id, Employee editEmployee) {
        Employee employee = retrieveEmployeeById(id);
        employee.setName(editEmployee.getName());
        employee.setEmail(editEmployee.getEmail());
        employee.setUpdatedAt(LocalDateTime.now());

        return employeeRepository.save(employee);
    }

    public Employee changeEmployeeStatus(UUID id) {
        Employee employee = retrieveEmployeeById(id);

        EmployeeStatus newStatus = employee.getStatus().equals(EmployeeStatus.INACTIVE) ?
                EmployeeStatus.ACTIVE : EmployeeStatus.INACTIVE;
        employee.setStatus(newStatus);
        employee.setUpdatedAt(LocalDateTime.now());

        return employeeRepository.save(employee);
    }
}
