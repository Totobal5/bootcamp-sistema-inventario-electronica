package com.bootcamp.inventario.model.enums;

/**
 * Estado del ciclo de vida de una revisión de PCB
 */
public enum LifecycleStatus {
    /**
     * Diseño en borrador, en proceso de creación
     */
    DRAFT,
    
    /**
     * Prototipo, versión para pruebas
     */
    PROTOTYPE,
    
    /**
     * En producción activa
     */
    PRODUCTION,
    
    /**
     * Obsoleto, no se recomienda usar
     */
    DEPRECATED
}
