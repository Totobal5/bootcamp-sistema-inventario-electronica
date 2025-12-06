package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error estandarizada")
public class ErrorResponse {
    
    @Schema(description = "Timestamp del error", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;
    
    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    private String error;
    
    @Schema(description = "Mensaje de error descriptivo", example = "Componente no encontrado con ID: 123")
    private String message;
    
    @Schema(description = "Ruta del endpoint donde ocurrió el error", example = "/api/componentes/123")
    private String path;
    
    @Schema(description = "Lista de errores de validación (opcional)")
    private List<ValidationError> validationErrors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalle de error de validación")
    public static class ValidationError {
        
        @Schema(description = "Campo que falló la validación", example = "nombre")
        private String field;
        
        @Schema(description = "Mensaje de error de validación", example = "El nombre no puede estar vacío")
        private String message;
    }
}
