package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para placas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información completa de una placa")
public class PlacaResponse {

    @Schema(description = "ID de la placa", example = "1")
    private Long id;

    @Schema(description = "Nombre de la placa", example = "Placa Arduino Uno Compatible")
    private String nombre;

    @Schema(description = "Descripción de la placa")
    private String descripcion;

    @Schema(description = "URL de la imagen")
    private String imagenUrl;

    @Schema(description = "Lista de componentes necesarios")
    private List<PlacaComponenteResponse> componentes;

    @Schema(description = "Indica si todos los componentes están disponibles en stock")
    private Boolean stockDisponible;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
