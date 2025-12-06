package com.bootcamp.inventario.model;

import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Tarea - Tareas asignadas a operadores para solicitudes de armado
 */
@Entity
@Table(name = "tareas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_armado_id", nullable = false)
    private SolicitudArmado solicitudArmado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operador;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Método para iniciar la tarea
     */
    public void iniciar() {
        this.estado = EstadoSolicitud.EN_PROCESO;
        this.fechaInicio = LocalDateTime.now();
    }

    /**
     * Método para pausar la tarea
     */
    public void pausar(String observaciones) {
        this.estado = EstadoSolicitud.PAUSADO;
        this.observaciones = observaciones;
    }

    /**
     * Método para completar la tarea
     */
    public void completar(String observaciones) {
        this.estado = EstadoSolicitud.COMPLETADO;
        this.fechaFin = LocalDateTime.now();
        this.observaciones = observaciones;
    }
}
