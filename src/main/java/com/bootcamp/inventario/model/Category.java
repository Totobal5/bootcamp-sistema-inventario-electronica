package com.bootcamp.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Category - Categoría de componentes electrónicos
 * Define categorías con plantillas de atributos dinámicos específicos
 */
@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único de la categoría
     * Ejemplos: "Resistencias", "Capacitores", "Microcontroladores", "Conectores"
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Descripción de la categoría
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Plantilla de esquema para atributos dinámicos en formato JSON
     * Define la estructura de especificaciones técnicas que deben tener los componentes de esta categoría
     * 
     * Ejemplo para Resistencias:
     * {
     *   "fields": [
     *     {"name": "resistance", "type": "string", "unit": "Ω", "required": true},
     *     {"name": "tolerance", "type": "string", "unit": "%", "required": true},
     *     {"name": "power", "type": "string", "unit": "W", "required": true},
     *     {"name": "temperature_coefficient", "type": "string", "unit": "ppm/°C", "required": false}
     *   ]
     * }
     * 
     * Ejemplo para Capacitores:
     * {
     *   "fields": [
     *     {"name": "capacitance", "type": "string", "unit": "F", "required": true},
     *     {"name": "voltage", "type": "string", "unit": "V", "required": true},
     *     {"name": "tolerance", "type": "string", "unit": "%", "required": true},
     *     {"name": "dielectric", "type": "string", "required": false}
     *   ]
     * }
     */
    @Column(columnDefinition = "jsonb")
    private String schemaTemplate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
