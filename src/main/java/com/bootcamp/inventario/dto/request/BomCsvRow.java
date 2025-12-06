package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa una fila del CSV de BOM importado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Fila de un archivo BOM CSV")
public class BomCsvRow {

    @NotBlank(message = "Los designadores son obligatorios")
    @Schema(description = "Designadores del componente (ej: 'R1, R2, R5')", 
            example = "R1, R2, R3", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String designators;

    @NotBlank(message = "El MPN es obligatorio")
    @Schema(description = "Manufacturer Part Number", 
            example = "RC0603JR-0710KL", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String mpn;

    @Positive(message = "La cantidad debe ser positiva")
    @Schema(description = "Cantidad requerida", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "Descripción del componente", example = "Resistencia 10kΩ ±5% 0603")
    private String description;

    @Schema(description = "Valor técnico", example = "10kΩ")
    private String value;

    @Schema(description = "Footprint/Encapsulado", example = "0603")
    private String footprint;

    @Schema(description = "Fabricante", example = "Yageo")
    private String manufacturer;
}
