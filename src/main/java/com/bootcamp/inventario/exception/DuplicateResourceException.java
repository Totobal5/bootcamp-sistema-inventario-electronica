package com.bootcamp.inventario.exception;

public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
        super(String.format(
            "%s ya existe con %s: %s",
            resourceName, fieldName, fieldValue
        ));
    }
}
