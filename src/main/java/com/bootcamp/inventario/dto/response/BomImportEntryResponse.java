package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para entradas de BOM importado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entrada de un BOM importado desde CSV")
public class BomImportEntryResponse {

    @Schema(description = "ID de la entrada", example = "1")
    private Long id;

    @Schema(description = "Designadores", example = "R1, R2, R5")
    private String designators;

    @Schema(description = "MPN del CSV", example = "RC0603JR-0710KL")
    private String mpn;

    @Schema(description = "Cantidad requerida", example = "10")
    private Integer quantity;

    @Schema(description = "Descripción del CSV")
    private String description;

    @Schema(description = "Valor técnico", example = "10kΩ")
    private String value;

    @Schema(description = "Footprint del CSV", example = "0603")
    private String footprint;

    @Schema(description = "Fabricante del CSV", example = "Yageo")
    private String manufacturer;

    // Matching
    @Schema(description = "ID del componente vinculado del inventario")
    private Long componenteId;

    @Schema(description = "Nombre del componente vinculado")
    private String componenteNombre;

    @Schema(description = "Se encontró match", example = "true")
    private Boolean matched;

    @Schema(description = "Nivel de confianza del match (0.0-1.0)", example = "1.0")
    private Double matchConfidence;

    @Schema(description = "Notas del matching", example = "Match exacto por MPN")
    private String matchNotes;

    @Schema(description = "Verificado manualmente", example = "false")
    private Boolean manuallyVerified;

    @Schema(description = "Necesita revisión manual", example = "false")
    private Boolean needsManualReview;

    @Schema(description = "Stock actual del componente (si está vinculado)")
    private Integer stockActual;

    @Schema(description = "Stock disponible suficiente", example = "true")
    private Boolean stockSufficient;
}
