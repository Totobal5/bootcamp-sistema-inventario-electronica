package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para el resultado de importación de BOM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resultado de la importación de un BOM desde CSV")
public class BomImportResponse {

    @Schema(description = "Total de filas procesadas", example = "25")
    private Integer totalRows;

    @Schema(description = "Filas con match exitoso", example = "20")
    private Integer matchedRows;

    @Schema(description = "Filas sin match (requieren atención)", example = "5")
    private Integer unmatchedRows;

    @Schema(description = "Porcentaje de éxito del matching", example = "80.0")
    private Double matchSuccessRate;

    @Schema(description = "Lista de componentes no encontrados")
    private List<String> notFoundMpns;

    @Schema(description = "Warnings generados durante la importación")
    private List<String> warnings;

    @Schema(description = "Componentes con stock insuficiente")
    private List<String> insufficientStockWarnings;

    @Schema(description = "Detalles de todas las entradas importadas")
    private List<BomImportEntryResponse> entries;

    @Schema(description = "Importación completada exitosamente", example = "true")
    private Boolean success;

    @Schema(description = "Mensaje resumen")
    private String message;

    /**
     * Factory method para crear una respuesta exitosa
     */
    public static BomImportResponse success(int total, int matched, int unmatched, 
                                           List<BomImportEntryResponse> entries) {
        double successRate = total > 0 ? (matched * 100.0 / total) : 0.0;
        
        return BomImportResponse.builder()
                .totalRows(total)
                .matchedRows(matched)
                .unmatchedRows(unmatched)
                .matchSuccessRate(successRate)
                .entries(entries)
                .success(true)
                .message(String.format("BOM importado: %d/%d componentes vinculados (%.1f%%)", 
                                      matched, total, successRate))
                .build();
    }
}
