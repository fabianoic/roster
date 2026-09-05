package com.ficsolution.roster.exception;

public class EmployeeHasNotPermissionException extends RuntimeException {
    public EmployeeHasNotPermissionException(String message) {
        super(message);
    }
}
