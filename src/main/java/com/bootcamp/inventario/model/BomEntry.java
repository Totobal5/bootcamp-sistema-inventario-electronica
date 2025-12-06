package com.bootcamp.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidad BomEntry - Entrada del BOM (Bill of Materials)
 * Entidad intermedia para la relación ManyToMany entre PcbRevision y ComponenteElectronico
 * Almacena la cantidad necesaria y designadores de cada componente para una revisión de PCB
 */
@Entity
@Table(name = "bom_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Diseño de PCB al que pertenece (para componentes directos del diseño)
     * Opcional - se usa cuando el BOM está asociado directamente al diseño
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcb_design_id")
    private PcbDesign pcbDesign;

    /**
     * Revisión de PCB a la que pertenece esta entrada del BOM
     * Opcional - se usa cuando el BOM está asociado a una revisión específica
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcb_revision_id")
    private PcbRevision pcbRevision;

    /**
     * Componente electrónico requerido
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "componente_id", nullable = false)
    private ComponenteElectronico componente;

    /**
     * Cantidad necesaria del componente
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Designadores o referencias del componente en la PCB
     * Ejemplo: "R1,R2,R3" o "C10,C11" o "U1"
     * Almacena las referencias de posición del componente en el esquemático y PCB
     */
    @Size(max = 500, message = "Los designadores no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String designators;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BomEntry)) return false;
        BomEntry that = (BomEntry) o;
        return (pcbDesign != null && pcbDesign.equals(that.pcbDesign) ||
                pcbRevision != null && pcbRevision.equals(that.pcbRevision)) &&
               componente != null && componente.equals(that.componente);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
