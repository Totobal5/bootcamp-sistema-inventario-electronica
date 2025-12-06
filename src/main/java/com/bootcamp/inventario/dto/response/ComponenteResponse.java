package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO de respuesta para componentes electrónicos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información de un componente electrónico")
public class ComponenteResponse {

    @Schema(description = "ID del componente", example = "1")
    private Long id;

    // ========== CAMPOS TÉCNICOS DE MANUFACTURA ==========
    @Schema(description = "Manufacturer Part Number (identificador único del fabricante)", 
            example = "RC0603JR-0710KL")
    private String mpn;

    @Schema(description = "Fabricante del componente", example = "Yageo")
    private String manufacturer;

    @Schema(description = "Encapsulado/Footprint del componente", example = "0603")
    private String footprint;

    @Schema(description = "URL del datasheet técnico", 
            example = "https://www.yageo.com/upload/media/product/app/datasheet/rchip/pyu-rc_group_51_rohs_l.pdf")
    private String datasheetUrl;

    @Schema(description = "Valor técnico del componente (resistencia, capacitancia, etc.)", 
            example = "10kΩ")
    private String valorTecnico;

    @Schema(description = "Especificaciones técnicas en formato JSON", 
            example = "{\"resistance\":\"10k\",\"tolerance\":\"5%\",\"power\":\"0.25W\"}")
    private Map<String, Object> specs;

    // ========== CAMPOS GENERALES ==========
    @Schema(description = "Nombre del componente", example = "Resistencia 10kΩ (0603)")
    private String nombre;

    @Schema(description = "Código interno/SKU", example = "RES-10K-001")
    private String codigoInterno;

    @Schema(description = "Descripción del componente", 
            example = "Resistencia de película gruesa 1/10W, tolerancia 5%")
    private String descripcion;

    @Schema(description = "Cantidad actual en stock", example = "150")
    private Integer stockActual;

    @Schema(description = "Stock mínimo de alerta", example = "10")
    private Integer stockMinimo;

    @Schema(description = "Categoría", example = "RESISTENCIA")
    private String categoria;

    @Schema(description = "Precio unitario", example = "50.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Ubicación física en almacén", example = "Estante A-3")
    private String ubicacion;

    @Schema(description = "URL de compra del componente", 
            example = "https://www.mouser.com/ProductDetail/...")
    private String urlCompra;

    @Schema(description = "URL de la imagen")
    private String imagenUrl;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
