package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para entradas del BOM (Bill of Materials)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Componente necesario en el BOM del diseño PCB")
public class BomEntryRequest {

    @NotNull(message = "El ID del componente es obligatorio")
    @Schema(description = "ID del componente electrónico", example = "1")
    private Long componenteId;

    @NotNull(message = "La cantidad necesaria es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad necesaria del componente", example = "2")
    private Integer cantidadNecesaria;

    @Schema(description = "Designadores del componente en la PCB", example = "R1,R2,R3")
    private String designators;
}
