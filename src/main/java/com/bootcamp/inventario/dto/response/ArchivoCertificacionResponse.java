package com.bootcamp.inventario.dto.response;

import com.bootcamp.inventario.model.enums.TipoArchivo;
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
@Schema(description = "Response de archivo de certificación")
public class ArchivoCertificacionResponse {
    
    @Schema(description = "ID del archivo", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del archivo", example = "certificado_calidad.pdf")
    private String nombreArchivo;
    
    @Schema(description = "Tipo de archivo", example = "CERTIFICACION")
    private TipoArchivo tipoArchivo;
    
    @Schema(description = "Tamaño del archivo en bytes", example = "524288")
    private Long tamanoBytes;
    
    @Schema(description = "URL del archivo", example = "/uploads/certificaciones/cert_1.pdf")
    private String archivoUrl;
    
    @Schema(description = "ID de la solicitud de armado asociada", example = "1")
    private Long solicitudArmadoId;
    
    @Schema(description = "Nombre de la placa de la solicitud", example = "Arduino UNO Clone")
    private String placaNombre;
    
    @Schema(description = "Cantidad de placas de la solicitud", example = "5")
    private Integer cantidadPlacas;
    
    @Schema(description = "Fecha de subida del archivo", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaCreacion;
}
