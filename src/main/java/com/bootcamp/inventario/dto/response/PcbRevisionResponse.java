package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para revisiones de PCB
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información detallada de una revisión de PCB")
public class PcbRevisionResponse {

    @Schema(description = "ID de la revisión", example = "1")
    private Long id;

    @Schema(description = "ID del diseño de PCB padre", example = "5")
    private Long pcbDesignId;

    @Schema(description = "Nombre del diseño de PCB padre", example = "ESP32 DevKit")
    private String pcbDesignName;

    @Schema(description = "Código de revisión", example = "v1.0")
    private String revisionCode;

    // Archivos
    @Schema(description = "Ruta al archivo Gerber")
    private String gerberPath;

    @Schema(description = "Ruta al archivo BOM")
    private String bomPath;

    @Schema(description = "Ruta al archivo Pick & Place")
    private String pickAndPlacePath;

    // Especificaciones técnicas
    @Schema(description = "Ancho de la placa en mm", example = "100.0")
    private BigDecimal width;

    @Schema(description = "Alto de la placa en mm", example = "80.0")
    private BigDecimal height;

    @Schema(description = "Número de capas", example = "2")
    private Integer layers;

    @Schema(description = "Espesor en mm", example = "1.6")
    private BigDecimal thickness;

    @Schema(description = "Acabado superficial", example = "ENIG")
    private String finish;

    @Schema(description = "Color de máscara", example = "GREEN")
    private String maskColor;

    @Schema(description = "Material", example = "FR4")
    private String material;

    @Schema(description = "Notas técnicas")
    private String technicalNotes;

    // Estado y aprobación
    @Schema(description = "Estado del ciclo de vida", example = "PROTOTYPE")
    private String lifecycleStatus;

    @Schema(description = "Fecha de aprobación")
    private LocalDateTime approvedAt;

    @Schema(description = "Usuario que aprobó")
    private String approvedBy;

    @Schema(description = "Está bloqueada para edición", example = "true")
    private Boolean locked;

    // BOM
    @Schema(description = "Número de componentes en el BOM manual")
    private Integer bomEntriesCount;

    @Schema(description = "Número de componentes en el BOM importado")
    private Integer bomImportEntriesCount;

    @Schema(description = "Componentes del BOM importado que necesitan revisión manual")
    private Integer unmatchedComponentsCount;

    // Auditoría
    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última modificación")
    private LocalDateTime updatedAt;

    // Lista de componentes (opcional, para endpoints detallados)
    @Schema(description = "Entradas del BOM importado (solo en endpoints detallados)")
    private List<BomImportEntryResponse> bomImportEntries;
}
