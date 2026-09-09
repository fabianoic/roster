package com.ficsolution.roster.config;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.service.EmployeeService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthenticationEventListener {

    private final EmployeeService employeeService;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    public AuthenticationEventListener(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();

        Employee employee = employeeService.retrieveEmployeeByEmail(email);

        if (!employee.isAccountLocked()) {
            int newAttempts = employee.getFailedAttempt() + 1;
            employee.setFailedAttempt(newAttempts);

            if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                employee.setAccountLocked(true);
                employee.setLockTime(LocalDateTime.now());
            }
        }
        employeeService.updateEmployeeLockInfo(employee);
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();

        Employee employee = employeeService.retrieveEmployeeByEmail(email);

        if (employee.getFailedAttempt() > 0) {
            employee.setFailedAttempt(0);
            employee.setLockTime(null);
            employeeService.updateEmployeeLockInfo(employee);
        }
    }
}
