package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para componentes electrónicos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información de un componente electrónico")
public class ComponenteResponse {

    @Schema(description = "ID del componente", example = "1")
    private Long id;

    @Schema(description = "Nombre del componente", example = "Resistencia 1K Ohm")
    private String nombre;

    @Schema(description = "Descripción del componente", 
            example = "Resistencia de carbón 1/4W, tolerancia 5%")
    private String descripcion;

    @Schema(description = "Cantidad actual en stock", example = "150")
    private Integer stockActual;

    @Schema(description = "Stock mínimo de alerta", example = "10")
    private Integer stockMinimo;

    @Schema(description = "Categoría", example = "Resistencias")
    private String categoria;

    @Schema(description = "Precio unitario", example = "50.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Ubicación física en almacén", example = "Estante A-3")
    private String ubicacion;

    @Schema(description = "URL de la imagen")
    private String imagenUrl;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
