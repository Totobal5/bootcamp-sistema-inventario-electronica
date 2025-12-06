package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear o actualizar solicitud de mecanizado")
public class SolicitudMecanizadoRequest {
    
    @NotBlank(message = "El nombre de la placa es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre de la placa a mecanizar", example = "Placa Arduino Compatible")
    private String nombrePlaca;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción de la placa", example = "Placa compatible con Arduino UNO con conexión USB-C")
    private String descripcion;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad de placas a mecanizar", example = "10")
    private Integer cantidad;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio total de la solicitud", example = "150000.00")
    private BigDecimal precio;
    
    @Size(max = 1000, message = "Las especificaciones técnicas no pueden exceder 1000 caracteres")
    @Schema(description = "Especificaciones técnicas de la placa", 
            example = "PCB 2 capas, acabado HASL, máscara verde, serigrafía blanca")
    private String especificacionesTecnicas;
}
