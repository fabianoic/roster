package com.ficsolution.roster.config;

import com.ficsolution.roster.service.EmployeeService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener {

    private final EmployeeService employeeService;

    public AuthenticationEventListener(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        if (event.getAuthentication() instanceof UsernamePasswordAuthenticationToken) {
            employeeService.registerLoginFailure(event.getAuthentication().getName());
        }
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof UsernamePasswordAuthenticationToken) {
            employeeService.registerLoginSuccess(event.getAuthentication().getName());
        }
    }
}
