package com.bootcamp.inventario.dto.response;

import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de solicitud de armado")
public class SolicitudArmadoResponse {
    
    @Schema(description = "ID de la solicitud", example = "1")
    private Long id;
    
    @Schema(description = "ID del diseño PCB", example = "3")
    private Long pcbDesignId;
    
    @Schema(description = "Nombre del diseño PCB", example = "Arduino UNO Clone")
    private String pcbDesignNombre;
    
    @Schema(description = "Cantidad de PCBs a armar", example = "5")
    private Integer cantidad;
    
    @Schema(description = "Observaciones", example = "Urgente - Entrega antes del viernes")
    private String observaciones;
    
    @Schema(description = "Estado actual de la solicitud", example = "PENDIENTE")
    private EstadoSolicitud estado;
    
    @Schema(description = "Indica si hay stock suficiente para armar", example = "true")
    private Boolean stockDisponible;
    
    @Schema(description = "ID del cliente que realizó la solicitud", example = "5")
    private Long clienteId;
    
    @Schema(description = "Nombre de usuario del cliente", example = "juan.perez")
    private String clienteUsername;
    
    @Schema(description = "Email del cliente", example = "juan.perez@email.com")
    private String clienteEmail;
    
    @Schema(description = "Lista de archivos de certificación asociados")
    private List<ArchivoCertificacionResponse> certificaciones;
    
    @Schema(description = "Fecha de creación de la solicitud", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última modificación", example = "2024-01-15T14:20:00")
    private LocalDateTime fechaActualizacion;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información resumida de archivo de certificación")
    public static class ArchivoCertificacionResponse {
        
        @Schema(description = "ID del archivo", example = "1")
        private Long id;
        
        @Schema(description = "Nombre del archivo", example = "certificado_calidad.pdf")
        private String nombreArchivo;
        
        @Schema(description = "Tipo de archivo", example = "CERTIFICACION")
        private String tipoArchivo;
        
        @Schema(description = "URL del archivo", example = "/uploads/certificaciones/cert_1.pdf")
        private String archivoUrl;
    }
}
