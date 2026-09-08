package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectConflictException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Employee createEmployee(Employee employee) {
        String normalizedEmail = employee.getEmail().trim().toLowerCase();

        if (employeeRepository.existsByEmail(normalizedEmail)) {
            throw new ObjectConflictException("E-mail", "This e-mail already exists.");
        }

        Role role = roleService.retrieveById(employee.getRole().getId());

        String hashedPassword = passwordEncoder.encode(employee.getPassword());

        employee.setRole(role);
        employee.setPassword(hashedPassword);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setEmail(normalizedEmail);


        return employeeRepository.save(employee);

    }

    public List<Employee> retrieveAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee retrieveEmployeeById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Employee", id.toString()));
    }

    public Employee updateEmployeeInfo(UUID id, Employee newEmployeeInfo) {
        Employee employee = retrieveEmployeeById(id);
        employee.setName(newEmployeeInfo.getName());

        Role newRole = roleService.retrieveById(newEmployeeInfo.getRole().getId());
        employee.setRole(newRole);
        employee.setEmail(newEmployeeInfo.getEmail());

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

    public Employee changeEmployeePassword(UUID id, String oldPassword, String newPassword) {
        Employee employee = retrieveEmployeeById(id);
        if (oldPassword.equals(employee.getPassword()) && isValidPassword(newPassword)) {
            employee.setPassword(newPassword);
            return employeeRepository.save(employee);
        }
        throw new ObjectConflictException("E-mail", "The old password is not correct!");
    }

    private boolean isValidPassword(String newPassword) {
        //develop more security validation
        return newPassword != null && !newPassword.isBlank();
    }
}
