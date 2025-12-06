package com.bootcamp.inventario.dto.request;

import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para actualizar el estado de una solicitud")
public class ActualizarEstadoRequest {
    
    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Nuevo estado de la solicitud", example = "EN_PROCESO")
    private EstadoSolicitud estado;
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Schema(description = "Observaciones opcionales sobre el cambio de estado", 
            example = "Iniciando proceso de fabricación")
    private String observaciones;
}
