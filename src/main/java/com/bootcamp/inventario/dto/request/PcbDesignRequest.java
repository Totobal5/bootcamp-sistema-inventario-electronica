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
 * DTO para crear/actualizar diseños de PCB
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un diseño de PCB")
public class PcbDesignRequest {

    @NotBlank(message = "El nombre del diseño es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Nombre del diseño de PCB", example = "Placa Arduino Uno Compatible")
    private String nombre;

    @Schema(description = "Descripción detallada del diseño",
            example = "Placa compatible con Arduino Uno R3, incluye microcontrolador ATmega328P")
    private String descripcion;

    @Schema(description = "URL de la imagen del diseño",
            example = "https://example.com/images/arduino-uno.jpg")
    private String imagenUrl;

    @NotEmpty(message = "El diseño debe tener al menos un componente")
    @Valid
    @Schema(description = "Lista de componentes necesarios para el diseño (BOM)")
    private List<BomEntryRequest> componentes;
}
