package com.bootcamp.inventario.model;

import com.bootcamp.inventario.model.enums.TipoArchivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad ArchivoCertificacion - Archivos de certificación e inspección
 * Subidos por operadores para solicitudes de armado
 */
@Entity
@Table(name = "archivos_certificacion")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoCertificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_armado_id", nullable = false)
    private SolicitudArmado solicitudArmado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoArchivo tipoArchivo;

    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Column(nullable = false, length = 255)
    private String nombreArchivo;

    @NotBlank(message = "La URL del archivo es obligatoria")
    @Column(nullable = false, length = 255)
    private String archivoUrl;

    @Column
    private Long tamanoBytes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
