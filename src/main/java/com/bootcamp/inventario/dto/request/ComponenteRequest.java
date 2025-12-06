package com.bootcamp.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear/actualizar componentes electrónicos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un componente electrónico")
public class ComponenteRequest {

    @NotBlank(message = "El nombre del componente es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Nombre del componente", example = "Resistencia 1K Ohm")
    private String nombre;

    @Size(max = 50, message = "El código interno no puede exceder 50 caracteres")
    @Schema(description = "Código interno/SKU del componente", example = "RES-1K-001")
    private String codigoInterno;

    @Schema(description = "Descripción detallada del componente", 
            example = "Resistencia de carbón 1/4W, tolerancia 5%")
    private String descripcion;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Cantidad actual en stock", example = "150")
    private Integer stockActual;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Schema(description = "Stock mínimo de alerta", example = "10")
    private Integer stockMinimo;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    @Schema(description = "Categoría del componente", example = "Resistencias")
    private String categoria;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    @Schema(description = "Precio unitario", example = "50.00")
    private BigDecimal precioUnitario;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    @Schema(description = "Ubicación física en almacén", example = "Estante A-3")
    private String ubicacion;

    @Size(max = 500, message = "La URL de compra no puede exceder 500 caracteres")
    @Schema(description = "URL de la tienda donde se compra el componente", 
            example = "https://www.digikey.com/product/RES123")
    private String urlCompra;

    @Schema(description = "URL de la imagen del componente", 
            example = "https://example.com/images/resistencia-1k.jpg")
    private String imagenUrl;

    // ===============================
    // CAMPOS TÉCNICOS MANUFACTURA
    // ===============================
    
    @NotBlank(message = "El MPN (Manufacturer Part Number) es obligatorio")
    @Size(max = 100, message = "El MPN no puede exceder 100 caracteres")
    @Schema(description = "MPN - Manufacturer Part Number (identificador único del fabricante)", 
            example = "RC0603JR-0710KL", required = true)
    private String mpn;

    @NotBlank(message = "El fabricante es obligatorio")
    @Size(max = 100, message = "El fabricante no puede exceder 100 caracteres")
    @Schema(description = "Nombre del fabricante", example = "Yageo", required = true)
    private String manufacturer;

    @NotBlank(message = "El footprint/encapsulado es obligatorio")
    @Size(max = 50, message = "El footprint no puede exceder 50 caracteres")
    @Schema(description = "Footprint/Encapsulado del componente", example = "0603", required = true)
    private String footprint;

    @Size(max = 500, message = "La URL del datasheet no puede exceder 500 caracteres")
    @Schema(description = "URL del datasheet técnico (PDF)", 
            example = "https://www.yageo.com/upload/media/product/products/datasheet/rchip/PYu-RC_Group_51_RoHS_L_12.pdf")
    private String datasheetUrl;

    @Size(max = 100, message = "El valor técnico no puede exceder 100 caracteres")
    @Schema(description = "Valor técnico principal del componente", 
            example = "10kΩ / 100nF / 3.3V")
    private String valorTecnico;

    @Schema(description = "Especificaciones técnicas en formato JSON", 
            example = "{\"resistance\":\"10k\",\"tolerance\":\"5%\",\"power\":\"0.25W\"}")
    private String specs;
}
