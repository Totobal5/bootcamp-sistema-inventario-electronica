package com.bootcamp.inventario.model;

import com.bootcamp.inventario.model.enums.LifecycleStatus;
import com.bootcamp.inventario.model.enums.PcbFinish;
import com.bootcamp.inventario.model.enums.MaskColor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad PcbRevision - Revisión específica de un diseño de PCB
 * Representa una versión específica del diseño con sus archivos y BOM
 */
@Entity
@Table(name = "pcb_revisions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"pcb_design_id", "revision_code"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcbRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Diseño de PCB al que pertenece esta revisión
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcb_design_id", nullable = false)
    private PcbDesign pcbDesign;

    /**
     * Código de revisión (ej: "v1.0", "rev-A", "2024-001")
     * Único dentro del mismo diseño
     */
    @NotBlank(message = "El código de revisión es obligatorio")
    @Size(max = 50, message = "El código de revisión no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String revisionCode;

    // ========== ARCHIVOS DE MANUFACTURA ==========
    
    /**
     * Ruta al archivo Gerber comprimido (.zip)
     */
    @Column(length = 500)
    private String gerberPath;

    /**
     * Ruta al archivo BOM (Bill of Materials) - CSV
     */
    @Column(length = 500)
    private String bomPath;

    /**
     * Ruta al archivo Pick & Place (Centroid file)
     */
    @Column(length = 500)
    private String pickAndPlacePath;

    // ========== ESPECIFICACIONES TÉCNICAS ==========
    
    /**
     * Ancho de la placa en milímetros
     */
    @Positive(message = "El ancho debe ser positivo")
    @Column(precision = 10, scale = 2)
    private BigDecimal width;

    /**
     * Alto de la placa en milímetros
     */
    @Positive(message = "El alto debe ser positivo")
    @Column(precision = 10, scale = 2)
    private BigDecimal height;

    /**
     * Número de capas de la PCB (2, 4, 6, etc.)
     */
    @Positive(message = "El número de capas debe ser positivo")
    @Column
    private Integer layers;

    /**
     * Espesor de la placa en milímetros (ej: 1.6mm, 1.0mm, 0.8mm)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal thickness;

    /**
     * Acabado superficial de la placa
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PcbFinish finish;

    /**
     * Color de la máscara antisoldante
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MaskColor maskColor;

    /**
     * Material del sustrato (ej: FR4, Rogers, Aluminio)
     */
    @Size(max = 50)
    @Column(length = 50)
    private String material;

    /**
     * Notas técnicas adicionales
     */
    @Column(columnDefinition = "TEXT")
    private String technicalNotes;

    // ========== CICLO DE VIDA Y APROBACIÓN ==========
    
    /**
     * Estado del ciclo de vida de la revisión
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LifecycleStatus lifecycleStatus = LifecycleStatus.DRAFT;

    /**
     * Fecha de aprobación formal de ingeniería
     */
    @Column
    private LocalDateTime approvedAt;

    /**
     * Usuario que aprobó formalmente la revisión
     * Una vez aprobada, la revisión NO puede ser editada
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Usuario approvedBy;

    /**
     * Indica si la revisión está bloqueada para edición
     * Se bloquea automáticamente al aprobar
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean locked = false;

    // ========== BILL OF MATERIALS ==========

    /**
     * Entradas del BOM manual (componentes agregados manualmente)
     * Relación OneToMany con BomEntry
     */
    @OneToMany(mappedBy = "pcbRevision", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BomEntry> bomEntries = new ArrayList<>();

    /**
     * Entradas del BOM importado desde archivo CSV
     * Incluye matching automático con inventario
     */
    @OneToMany(mappedBy = "pcbRevision", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BomImportEntry> bomImportEntries = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Método helper para agregar una entrada al BOM
     */
    public void addBomEntry(BomEntry bomEntry) {
        bomEntries.add(bomEntry);
        bomEntry.setPcbRevision(this);
    }

    /**
     * Método helper para remover una entrada del BOM
     */
    public void removeBomEntry(BomEntry bomEntry) {
        bomEntries.remove(bomEntry);
        bomEntry.setPcbRevision(null);
    }

    /**
     * Método para aprobar formalmente la revisión
     * Una vez aprobada, se bloquea para evitar modificaciones
     * 
     * @param usuario Usuario que aprueba (debe tener rol de ingeniero/admin)
     * @throws IllegalStateException si la revisión ya está aprobada
     */
    public void approve(Usuario usuario) {
        if (this.locked) {
            throw new IllegalStateException("Esta revisión ya está aprobada y bloqueada");
        }
        
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = usuario;
        this.locked = true;
        
        // Cambiar a PROTOTYPE si está en DRAFT
        if (this.lifecycleStatus == LifecycleStatus.DRAFT) {
            this.lifecycleStatus = LifecycleStatus.PROTOTYPE;
        }
    }

    /**
     * Verifica si la revisión está aprobada y bloqueada
     */
    public boolean isApproved() {
        return this.locked && this.approvedBy != null;
    }

    /**
     * Verifica si la revisión puede ser editada
     */
    public boolean canBeEdited() {
        return !this.locked;
    }

    /**
     * Método para promover la revisión a producción
     * Solo puede ser llamado si la revisión está aprobada
     */
    public void promoteToProduction() {
        if (!this.isApproved()) {
            throw new IllegalStateException("Solo revisiones aprobadas pueden pasar a producción");
        }
        this.lifecycleStatus = LifecycleStatus.PRODUCTION;
    }

    /**
     * Método para deprecar la revisión
     */
    public void deprecate() {
        this.lifecycleStatus = LifecycleStatus.DEPRECATED;
    }
}
