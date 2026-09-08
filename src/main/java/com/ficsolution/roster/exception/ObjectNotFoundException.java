package com.ficsolution.roster.exception;

import lombok.Getter;

@Getter
public class ObjectNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String identifier;


    public ObjectNotFoundException(String resourceName, String identifier) {
        super(resourceName.concat(" not found, identifier: " + identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }
}
