package com.bootcamp.inventario.dto.response;

import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de tarea")
public class TareaResponse {
    
    @Schema(description = "ID de la tarea", example = "1")
    private Long id;
    
    @Schema(description = "Título de la tarea", example = "Armar 5 placas Arduino UNO")
    private String titulo;
    
    @Schema(description = "Descripción de la tarea", example = "Ensamblar componentes...")
    private String descripcion;
    
    @Schema(description = "Estado actual de la tarea", example = "PENDIENTE")
    private EstadoSolicitud estado;
    
    @Schema(description = "Observaciones adicionales", example = "Componentes verificados")
    private String observaciones;
    
    @Schema(description = "ID de la solicitud de armado asociada", example = "1")
    private Long solicitudArmadoId;
    
    @Schema(description = "Nombre de la placa de la solicitud", example = "Arduino UNO Clone")
    private String placaNombre;
    
    @Schema(description = "Cantidad de placas a armar", example = "5")
    private Integer cantidadPlacas;
    
    @Schema(description = "ID del operador asignado", example = "2")
    private Long operadorId;
    
    @Schema(description = "Nombre de usuario del operador", example = "operador1")
    private String operadorUsername;
    
    @Schema(description = "Nombre completo del operador", example = "Juan Pérez")
    private String operadorNombre;
    
    @Schema(description = "Fecha de inicio de la tarea", example = "2024-01-15T09:00:00")
    private LocalDateTime fechaInicio;
    
    @Schema(description = "Fecha de finalización de la tarea", example = "2024-01-15T17:00:00")
    private LocalDateTime fechaFin;
    
    @Schema(description = "Fecha de creación de la tarea", example = "2024-01-15T08:30:00")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última modificación", example = "2024-01-15T14:20:00")
    private LocalDateTime fechaActualizacion;
}
