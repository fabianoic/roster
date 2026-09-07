package com.ficsolution.roster.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ObjectNotFoundException extends RuntimeException {
    private final String resourceName;
    private final UUID identifier;


    public ObjectNotFoundException(String resourceName, UUID identifier) {
        super(resourceName.concat(" not found, id: " + identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }
}
