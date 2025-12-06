package com.bootcamp.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidad intermedia para la relación ManyToMany entre Placa y ComponenteElectronico
 * Almacena la cantidad necesaria de cada componente para una placa
 */
@Entity
@Table(name = "placa_componentes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"placa_id", "componente_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacaComponente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placa_id", nullable = false)
    private Placa placa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "componente_id", nullable = false)
    private ComponenteElectronico componente;

    @NotNull(message = "La cantidad necesaria es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidadNecesaria;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlacaComponente)) return false;
        PlacaComponente that = (PlacaComponente) o;
        return placa != null && placa.equals(that.placa) &&
               componente != null && componente.equals(that.componente);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
