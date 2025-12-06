package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear/actualizar componentes electrónicos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un componente electrónico")
public class ComponenteRequest {

    @NotBlank(message = "El nombre del componente es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Nombre del componente", example = "Resistencia 1K Ohm")
    private String nombre;

    @Schema(description = "Descripción detallada del componente", 
            example = "Resistencia de carbón 1/4W, tolerancia 5%")
    private String descripcion;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Cantidad en stock", example = "150")
    private Integer stock;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    @Schema(description = "Categoría del componente", example = "Resistencias")
    private String categoria;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    @Schema(description = "Precio unitario", example = "50.00")
    private BigDecimal precio;

    @Schema(description = "URL de la imagen del componente", 
            example = "https://example.com/images/resistencia-1k.jpg")
    private String imagenUrl;
}
