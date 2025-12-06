package com.bootcamp.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad PcbDesign - Diseño de PCB (Printed Circuit Board)
 * Representa un diseño de placa que puede tener múltiples revisiones
 */
@Entity
@Table(name = "pcb_designs")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcbDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único del diseño de PCB
     */
    @NotBlank(message = "El nombre del diseño es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Descripción del diseño
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * URL de la imagen o vista previa del diseño
     */
    @Column(length = 255)
    private String imagenUrl;

    /**
     * Usuario propietario del diseño
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Usuario owner;

    /**
     * Componentes del diseño (BOM - Bill of Materials)
     * Relación OneToMany directa para compatibilidad con sistema existente
     */
    @OneToMany(mappedBy = "pcbDesign", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BomEntry> componentes = new ArrayList<>();

    /**
     * Lista de revisiones del diseño
     * Relación OneToMany con cascade para gestionar el ciclo de vida
     */
    @OneToMany(mappedBy = "pcbDesign", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PcbRevision> revisiones = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Método helper para agregar un componente al diseño
     */
    public void addComponente(ComponenteElectronico componente, Integer cantidad) {
        BomEntry bomEntry = new BomEntry();
        bomEntry.setPcbDesign(this);
        bomEntry.setComponente(componente);
        bomEntry.setQuantity(cantidad);
        componentes.add(bomEntry);
    }

    /**
     * Método helper para remover componentes del diseño
     */
    public void removeComponente(BomEntry bomEntry) {
        componentes.remove(bomEntry);
        bomEntry.setPcbDesign(null);
    }

    /**
     * Método helper para agregar una revisión al diseño
     */
    public void addRevision(PcbRevision revision) {
        revisiones.add(revision);
        revision.setPcbDesign(this);
    }

    /**
     * Método helper para remover una revisión del diseño
     */
    public void removeRevision(PcbRevision revision) {
        revisiones.remove(revision);
        revision.setPcbDesign(null);
    }
}
