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
 * Entidad Placa - Define una placa con sus componentes necesarios
 */
@Entity
@Table(name = "placas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Placa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la placa es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 255)
    private String imagenUrl;

    @OneToMany(mappedBy = "placa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlacaComponente> componentes = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Método helper para agregar componentes a la placa
     */
    public void addComponente(ComponenteElectronico componente, Integer cantidad) {
        PlacaComponente placaComponente = new PlacaComponente();
        placaComponente.setPlaca(this);
        placaComponente.setComponente(componente);
        placaComponente.setCantidadNecesaria(cantidad);
        componentes.add(placaComponente);
    }

    /**
     * Método helper para remover componentes de la placa
     */
    public void removeComponente(PlacaComponente placaComponente) {
        componentes.remove(placaComponente);
        placaComponente.setPlaca(null);
    }
}
