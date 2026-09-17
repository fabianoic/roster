package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectConflictException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.repository.EmployeeRepository;
import com.ficsolution.roster.web.dto.employee.ChangePasswordRequest;
import com.ficsolution.roster.web.dto.employee.CreateEmployeeRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    @Transactional
    public Employee createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        String normalizedEmail = createEmployeeRequest.email();

        if (employeeRepository.existsByEmail(normalizedEmail)) {
            throw new ObjectConflictException("E-mail", "This e-mail already exists.");
        }

        Role role = roleService.retrieveById(createEmployeeRequest.roleId());

        Employee employee = createEmployeeRequest.toEntity();
        String hashedPassword = passwordEncoder.encode(createEmployeeRequest.password());
        employee.setRole(role);
        employee.setPassword(hashedPassword);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setEmail(normalizedEmail);

        return employeeRepository.save(employee);

    }

    @Transactional(readOnly = true)
    public Page<Employee> retrieveAllEmployees(Specification<Employee> specification, Pageable pageable) {
        return employeeRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public Employee retrieveEmployeeById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Employee", id.toString()));
    }

    @Transactional
    public Employee updateEmployeeInfo(UUID id, Employee updateEmployeeRequest) {
        Employee employee = retrieveEmployeeById(id);

        if (!employee.getRole().getId().equals(updateEmployeeRequest.getRole().getId())) {
            Role newRole = roleService.retrieveById(updateEmployeeRequest.getRole().getId());
            employee.setRole(newRole);
        }
        employee.setName(updateEmployeeRequest.getName());
        employee.setEmail(updateEmployeeRequest.getEmail());
        employee.setUpdatedAt(LocalDateTime.now());

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee changeEmployeeStatus(UUID id) {
        Employee employee = retrieveEmployeeById(id);

        EmployeeStatus newStatus = employee.getStatus().equals(EmployeeStatus.INACTIVE) ?
                EmployeeStatus.ACTIVE : EmployeeStatus.INACTIVE;
        employee.setStatus(newStatus);
        employee.setUpdatedAt(LocalDateTime.now());

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee changeEmployeePassword(UUID id, ChangePasswordRequest changePasswordRequest) {
        if (isValidPassword(changePasswordRequest.oldPassword()) && isValidPassword(changePasswordRequest.newPassword())) {
            Employee employee = retrieveEmployeeById(id);
            if (passwordEncoder.matches(changePasswordRequest.oldPassword(), employee.getPassword())) {
                employee.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
                return employeeRepository.save(employee);
            }
            throw new ObjectConflictException("Employee", "The old password is not correct!");
        }
        throw new ObjectConflictException("Employee", "The old password or the new password is not a valid password!");
    }

    private boolean isValidPassword(String password) {
        //develop more security validation
        return password != null && !password.isBlank();
    }

    @Transactional(readOnly = true)
    public Employee retrieveEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("Employee", email));
    }

    @Transactional
    public void registerLoginSuccess(String email) {
        Employee employee = employeeRepository
                .findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("Employee", email));

        if (employee.getFailedAttempt() > 0) {
            employee.setFailedAttempt(0);
            employee.setLockTime(null);
        }
    }

    @Transactional
    public void registerLoginFailure(String email) {
        Employee employee = employeeRepository
                .findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("Employee", email));

        if (!employee.isAccountLocked()) {
            int newAttempts = employee.getFailedAttempt() + 1;
            employee.setFailedAttempt(newAttempts);

            if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                employee.setAccountLocked(true);
                employee.setLockTime(LocalDateTime.now());
            }
        }
    }
}
