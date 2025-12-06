package com.bootcamp.inventario.util;

/**
 * Clase de constantes utilizadas en toda la aplicación
 */
public class Constants {
    
    // JWT
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    
    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OPERADOR = "OPERADOR";
    public static final String ROLE_CLIENTE = "CLIENTE";
    
    // Mensajes
    public static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    public static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";
    public static final String ACCESO_DENEGADO = "Acceso denegado";
    public static final String RECURSO_NO_ENCONTRADO = "Recurso no encontrado";
    public static final String STOCK_INSUFICIENTE = "Stock insuficiente para completar la operación";
    
    // File upload
    public static final long MAX_FILE_SIZE = 10485760; // 10MB
    public static final String[] ALLOWED_FILE_EXTENSIONS = {".pdf", ".jpg", ".jpeg", ".png", ".gerber", ".gbr"};
    
    private Constants() {
        throw new IllegalStateException("Clase de constantes");
    }
}
