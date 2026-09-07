package com.ficsolution.roster.exception;

import lombok.Getter;

@Getter
public class ObjectConflictException extends RuntimeException {
    private final String resourceName;
    private final String reason;


    public ObjectConflictException(String resourceName, String reason) {
        super(resourceName.concat(" has a conflict: ").concat(reason));
        this.resourceName = resourceName;
        this.reason = reason;
    }
}
