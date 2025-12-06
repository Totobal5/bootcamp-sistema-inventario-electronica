package com.bootcamp.inventario.exception;

public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String componentName, int available, int required) {
        super(String.format(
            "Stock insuficiente del componente '%s'. Disponible: %d, Requerido: %d",
            componentName, available, required
        ));
    }
    
    public InsufficientStockException(Long componentId, int available, int required) {
        super(String.format(
            "Stock insuficiente del componente ID %d. Disponible: %d, Requerido: %d",
            componentId, available, required
        ));
    }
}
