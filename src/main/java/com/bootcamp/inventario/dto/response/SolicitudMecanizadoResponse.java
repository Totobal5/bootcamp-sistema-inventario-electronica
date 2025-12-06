package com.bootcamp.inventario.dto.response;

import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de solicitud de mecanizado")
public class SolicitudMecanizadoResponse {
    
    @Schema(description = "ID de la solicitud", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la placa", example = "Placa Arduino Compatible")
    private String nombrePlaca;
    
    @Schema(description = "Descripción de la placa", example = "Placa compatible con Arduino UNO")
    private String descripcion;
    
    @Schema(description = "Cantidad de placas solicitadas", example = "10")
    private Integer cantidad;
    
    @Schema(description = "Precio total", example = "150000.00")
    private BigDecimal precio;
    
    @Schema(description = "Especificaciones técnicas", example = "PCB 2 capas, HASL")
    private String especificacionesTecnicas;
    
    @Schema(description = "Estado actual de la solicitud", example = "SOLICITADO")
    private EstadoSolicitud estado;
    
    @Schema(description = "Versión del archivo Gerber", example = "1")
    private Integer versionGerber;
    
    @Schema(description = "URL del archivo Gerber", example = "/uploads/gerber/solicitud_1_v1.zip")
    private String archivoGerberUrl;
    
    @Schema(description = "ID del cliente que realizó la solicitud", example = "5")
    private Long clienteId;
    
    @Schema(description = "Nombre de usuario del cliente", example = "juan.perez")
    private String clienteUsername;
    
    @Schema(description = "Email del cliente", example = "juan.perez@email.com")
    private String clienteEmail;
    
    @Schema(description = "Fecha de creación de la solicitud", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última modificación", example = "2024-01-15T14:20:00")
    private LocalDateTime fechaActualizacion;
}
