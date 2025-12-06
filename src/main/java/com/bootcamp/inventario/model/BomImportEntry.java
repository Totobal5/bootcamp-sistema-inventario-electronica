package com.bootcamp.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad BomImportEntry - Entrada de BOM importada desde archivo CSV
 * Representa una línea del BOM importado con su vinculación al componente del inventario
 */
@Entity
@Table(name = "bom_import_entries",
       indexes = {
           @Index(name = "idx_bom_import_revision", columnList = "pcb_revision_id"),
           @Index(name = "idx_bom_import_mpn", columnList = "mpn")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomImportEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Revisión de PCB a la que pertenece esta entrada del BOM
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcb_revision_id", nullable = false)
    @NotNull
    private PcbRevision pcbRevision;

    // ========== DATOS DEL CSV IMPORTADO ==========
    
    /**
     * Designadores/Referencias del componente en la PCB
     * Ejemplo: "R1, R2, R5" o "C10"
     */
    @NotBlank(message = "Los designadores son obligatorios")
    @Column(nullable = false, length = 500)
    private String designators;

    /**
     * Manufacturer Part Number del CSV
     * Campo crítico para el matching con el inventario
     */
    @NotBlank(message = "El MPN es obligatorio")
    @Column(nullable = false, length = 100)
    private String mpn;

    /**
     * Cantidad requerida del componente
     */
    @Positive(message = "La cantidad debe ser positiva")
    @Column(nullable = false)
    @NotNull
    private Integer quantity;

    /**
     * Descripción del componente en el CSV (opcional)
     */
    @Column(length = 500)
    private String description;

    /**
     * Valor técnico del componente (ej: "10kΩ", "100nF")
     * Tomado del CSV
     */
    @Column(length = 100)
    private String value;

    /**
     * Footprint/Encapsulado del componente en el CSV
     */
    @Column(length = 50)
    private String footprint;

    /**
     * Fabricante especificado en el CSV
     */
    @Column(length = 100)
    private String manufacturer;

    // ========== VINCULACIÓN CON INVENTARIO ==========
    
    /**
     * Componente del inventario vinculado (si se encontró match)
     * Null si no se pudo matchear automáticamente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componente_id")
    private ComponenteElectronico componente;

    /**
     * Indica si se encontró un match automático con el inventario
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean matched = false;

    /**
     * Nivel de confianza del match (0.0 - 1.0)
     * 1.0 = Match exacto por MPN
     * 0.5-0.99 = Match parcial por similitud
     * null = No match
     */
    @Column(precision = 3, scale = 2)
    private Double matchConfidence;

    /**
     * Notas sobre el matching (ej: "Match exacto", "No encontrado", "MPN similar: XXX")
     */
    @Column(length = 500)
    private String matchNotes;

    /**
     * Indica si la entrada fue verificada/corregida manualmente
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean manuallyVerified = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========== MÉTODOS HELPER ==========
    
    /**
     * Vincula esta entrada del BOM con un componente del inventario
     */
    public void linkToComponent(ComponenteElectronico componente, Double confidence, String notes) {
        this.componente = componente;
        this.matched = true;
        this.matchConfidence = confidence;
        this.matchNotes = notes;
    }

    /**
     * Marca como no encontrado
     */
    public void markAsNotFound(String notes) {
        this.componente = null;
        this.matched = false;
        this.matchConfidence = null;
        this.matchNotes = notes;
    }

    /**
     * Marca como verificado manualmente
     */
    public void markAsManuallyVerified() {
        this.manuallyVerified = true;
    }

    /**
     * Verifica si este componente necesita atención manual
     */
    public boolean needsManualReview() {
        return !matched || (matchConfidence != null && matchConfidence < 1.0 && !manuallyVerified);
    }
}
