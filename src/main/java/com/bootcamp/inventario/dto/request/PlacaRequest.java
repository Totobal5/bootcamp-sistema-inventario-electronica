package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear/actualizar placas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar una placa")
public class PlacaRequest {

    @NotBlank(message = "El nombre de la placa es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Nombre de la placa", example = "Placa Arduino Uno Compatible")
    private String nombre;

    @Schema(description = "Descripción detallada de la placa",
            example = "Placa compatible con Arduino Uno R3, incluye microcontrolador ATmega328P")
    private String descripcion;

    @Schema(description = "URL de la imagen de la placa",
            example = "https://example.com/images/arduino-uno.jpg")
    private String imagenUrl;

    @NotEmpty(message = "La placa debe tener al menos un componente")
    @Valid
    @Schema(description = "Lista de componentes necesarios para la placa")
    private List<PlacaComponenteRequest> componentes;
}
