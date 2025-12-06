package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request para crear tarea")
public class TareaRequest {
    
    @NotNull(message = "El ID de la solicitud de armado es obligatorio")
    @Schema(description = "ID de la solicitud de armado", example = "1")
    private Long solicitudArmadoId;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Schema(description = "Título de la tarea", example = "Armar 5 placas Arduino UNO")
    private String titulo;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción detallada de la tarea", 
            example = "Ensamblar componentes en las placas según especificaciones técnicas")
    private String descripcion;
    
    @NotNull(message = "El ID del operador es obligatorio")
    @Schema(description = "ID del operador asignado", example = "2")
    private Long operadorId;
}
