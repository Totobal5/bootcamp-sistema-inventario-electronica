package com.bootcamp.inventario.model;

import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad SolicitudArmado - Solicitudes de armado de placas
 * Verifica stock de componentes antes de crear
 */
@Entity
@Table(name = "solicitudes_armado")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudArmado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "placa_id", nullable = false)
    private Placa placa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id")
    private Usuario operador;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "solicitudArmado", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ArchivoCertificacion> certificaciones = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Método helper para agregar certificaciones
     */
    public void addCertificacion(ArchivoCertificacion certificacion) {
        certificaciones.add(certificacion);
        certificacion.setSolicitudArmado(this);
    }

    /**
     * Método helper para remover certificaciones
     */
    public void removeCertificacion(ArchivoCertificacion certificacion) {
        certificaciones.remove(certificacion);
        certificacion.setSolicitudArmado(null);
    }
}
