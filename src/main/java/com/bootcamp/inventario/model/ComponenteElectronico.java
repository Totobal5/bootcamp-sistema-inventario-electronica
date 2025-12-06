package com.bootcamp.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad ComponenteElectronico para gestión de inventario
 */
@Entity
@Table(name = "componentes_electronicos")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponenteElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del componente es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 50, message = "El código interno no puede exceder 50 caracteres")
    @Column(length = 50, unique = true)
    private String codigoInterno;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private Integer stockActual = 0;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column
    @Builder.Default
    private Integer stockMinimo = 0;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    @Column(length = 50)
    private String categoria;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    @Column(length = 100)
    private String ubicacion;

    @Size(max = 500, message = "La URL de compra no puede exceder 500 caracteres")
    @Column(length = 500)
    private String urlCompra;

    @Column(length = 255)
    private String imagenUrl;

    // ===============================
    // CAMPOS TÉCNICOS MANUFACTURA
    // ===============================
    
    /**
     * MPN - Manufacturer Part Number (Número de parte del fabricante)
     * Campo único y obligatorio para identificación técnica
     */
    @NotBlank(message = "El MPN es obligatorio")
    @Size(max = 100, message = "El MPN no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String mpn;

    /**
     * Footprint/Encapsulado del componente (ej: "0805", "SOIC-8", "QFN-32")
     */
    @Size(max = 50, message = "El footprint no puede exceder 50 caracteres")
    @Column(length = 50)
    private String footprint;

    /**
     * Fabricante del componente
     */
    @Size(max = 100, message = "El fabricante no puede exceder 100 caracteres")
    @Column(length = 100)
    private String manufacturer;

    /**
     * URL del datasheet técnico
     */
    @Size(max = 500, message = "La URL del datasheet no puede exceder 500 caracteres")
    @Column(length = 500)
    private String datasheetUrl;

    /**
     * Valor técnico del componente (ej: "10kΩ", "100nF", "3.3V")
     * Utilizado para autogenerar nombres descriptivos
     */
    @Size(max = 100, message = "El valor técnico no puede exceder 100 caracteres")
    @Column(length = 100)
    private String valorTecnico;

    /**
     * Especificaciones técnicas en formato JSON
     * Permite almacenar atributos dinámicos key-value según categoría
     * Ejemplo: {"voltage": "5V", "current": "100mA", "tolerance": "5%"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> specs;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
