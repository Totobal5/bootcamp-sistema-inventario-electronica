package com.bootcamp.inventario.exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException() {
        super("No tienes autorización para realizar esta acción");
    }
}
