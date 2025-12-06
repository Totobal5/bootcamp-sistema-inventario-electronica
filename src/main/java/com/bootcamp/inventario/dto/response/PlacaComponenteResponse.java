package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para componentes de una placa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Componente necesario en una placa")
public class PlacaComponenteResponse {

    @Schema(description = "ID del componente", example = "1")
    private Long componenteId;

    @Schema(description = "Nombre del componente", example = "Resistencia 1K Ohm")
    private String componenteNombre;

    @Schema(description = "Categoría del componente", example = "Resistencias")
    private String componenteCategoria;

    @Schema(description = "Cantidad necesaria", example = "2")
    private Integer cantidadNecesaria;

    @Schema(description = "Stock disponible del componente", example = "150")
    private Integer stockDisponible;
}
