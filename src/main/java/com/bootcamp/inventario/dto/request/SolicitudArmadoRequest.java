package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear solicitud de armado")
public class SolicitudArmadoRequest {
    
    @NotNull(message = "El ID de la placa es obligatorio")
    @Schema(description = "ID de la placa a armar", example = "1")
    private Long placaId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad de placas a armar", example = "5")
    private Integer cantidad;
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Schema(description = "Observaciones adicionales", example = "Urgente - Entrega antes del viernes")
    private String observaciones;
}
