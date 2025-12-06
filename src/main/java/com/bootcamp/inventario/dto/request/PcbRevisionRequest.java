package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una nueva revisión de PCB
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para crear una nueva revisión de PCB")
public class PcbRevisionRequest {

    @NotBlank(message = "El código de revisión es obligatorio")
    @Schema(description = "Código de revisión", example = "v1.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private String revisionCode;

    @Schema(description = "Ancho de la placa en mm", example = "100.0")
    private Double width;

    @Schema(description = "Alto de la placa en mm", example = "80.0")
    private Double height;

    @Schema(description = "Número de capas", example = "2")
    private Integer layers;

    @Schema(description = "Espesor de la placa en mm", example = "1.6")
    private Double thickness;

    @Schema(description = "Acabado superficial", example = "ENIG")
    private String finish;

    @Schema(description = "Color de la máscara", example = "GREEN")
    private String maskColor;

    @Schema(description = "Material del sustrato", example = "FR4")
    private String material;

    @Schema(description = "Notas técnicas adicionales")
    private String technicalNotes;
}
