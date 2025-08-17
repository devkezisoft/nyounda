package com.kezisoft.nyounda.application.shared.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested entity is not found.
 * This is a runtime exception that can be used to indicate that a specific entity
 * (like a category, user, etc.) could not be located in the system.
 */
@Getter
public class NotFoundException extends RuntimeException {
    protected String defaultMessage;
    protected String entityName;
    protected String errorKey;

    public NotFoundException(String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage);
        this.defaultMessage = defaultMessage;
        this.entityName = entityName;
        this.errorKey = errorKey;
    }
}
