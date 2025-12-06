package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para relacionar un componente con una placa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Componente necesario para una placa")
public class PlacaComponenteRequest {

    @NotNull(message = "El ID del componente es obligatorio")
    @Schema(description = "ID del componente electrónico", example = "1")
    private Long componenteId;

    @NotNull(message = "La cantidad necesaria es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad necesaria del componente", example = "2")
    private Integer cantidadNecesaria;
}
