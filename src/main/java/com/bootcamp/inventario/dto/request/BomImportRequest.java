package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para importar un archivo BOM (Bill of Materials) desde CSV
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para importar un BOM desde CSV")
public class BomImportRequest {

    @NotEmpty(message = "El BOM no puede estar vacío")
    @Schema(description = "Lista de filas del CSV parseado", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<BomCsvRow> rows;

    @Schema(description = "Sobrescribir BOM existente si hay uno", example = "false")
    @Builder.Default
    private Boolean overwrite = false;

    @Schema(description = "Nivel mínimo de confianza para auto-match (0.0-1.0)", example = "0.8")
    @Builder.Default
    private Double minimumConfidence = 0.8;
}
