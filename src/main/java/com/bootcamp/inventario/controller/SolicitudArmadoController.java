package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.ActualizarEstadoRequest;
import com.bootcamp.inventario.dto.request.SolicitudArmadoRequest;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.dto.response.SolicitudArmadoResponse;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import com.bootcamp.inventario.service.ISolicitudArmadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-armado")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Solicitudes de Armado", description = "Gestión de solicitudes de armado de diseños PCB")
public class SolicitudArmadoController {

    private final ISolicitudArmadoService solicitudService;

    @GetMapping
    @Operation(summary = "Obtener todas las solicitudes de armado",
               description = "ADMIN/OPERADOR ven todas, CLIENTE solo las propias")
    public ResponseEntity<List<SolicitudArmadoResponse>> getAllSolicitudes(Authentication authentication) {
        log.debug("GET /api/solicitudes-armado - Usuario: {}", authentication.getName());
        
        // Si es CLIENTE, solo mostrar sus propias solicitudes
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENTE"))) {
            List<SolicitudArmadoResponse> solicitudes = 
                    solicitudService.findAll().stream()
                            .filter(s -> s.getClienteUsername().equals(authentication.getName()))
                            .toList();
            return ResponseEntity.ok(solicitudes);
        }
        
        // ADMIN/OPERADOR ven todas
        return ResponseEntity.ok(solicitudService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por ID",
               description = "ADMIN/OPERADOR pueden ver cualquiera, CLIENTE solo las propias")
    public ResponseEntity<SolicitudArmadoResponse> getSolicitudById(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id,
            Authentication authentication) {
        log.debug("GET /api/solicitudes-armado/{} - Usuario: {}", id, authentication.getName());
        
        SolicitudArmadoResponse solicitud = solicitudService.findById(id);
        
        // Si es CLIENTE, verificar que sea propietario
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENTE"))) {
            if (!solicitud.getClienteUsername().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        return ResponseEntity.ok(solicitud);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Obtener solicitudes por estado",
               description = "Solo ADMIN/OPERADOR")
    public ResponseEntity<List<SolicitudArmadoResponse>> getSolicitudesByEstado(
            @PathVariable @Parameter(description = "Estado de la solicitud") EstadoSolicitud estado) {
        log.debug("GET /api/solicitudes-armado/estado/{}", estado);
        return ResponseEntity.ok(solicitudService.findByEstado(estado));
    }

    @GetMapping("/pcb-design/{pcbDesignId}")
    @Operation(summary = "Obtener solicitudes de un diseño PCB específico")
    public ResponseEntity<List<SolicitudArmadoResponse>> getSolicitudesByPcbDesign(
            @PathVariable @Parameter(description = "ID del diseño PCB") Long pcbDesignId) {
        log.debug("GET /api/solicitudes-armado/pcb-design/{}", pcbDesignId);
        return ResponseEntity.ok(solicitudService.findByPcbDesignId(pcbDesignId));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear nueva solicitud de armado",
               description = "Solo CLIENTE puede crear solicitudes. Valida stock automáticamente")
    public ResponseEntity<SolicitudArmadoResponse> createSolicitud(
            @Valid @RequestBody SolicitudArmadoRequest request,
            Authentication authentication) {
        log.debug("POST /api/solicitudes-armado - Usuario: {}", authentication.getName());
        
        SolicitudArmadoResponse created = solicitudService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Actualizar estado de la solicitud",
               description = "Solo ADMIN/OPERADOR pueden cambiar estados. No usar para COMPLETAR, usar /confirmar-armado")
    public ResponseEntity<SolicitudArmadoResponse> actualizarEstado(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        log.debug("PATCH /api/solicitudes-armado/{}/estado - Nuevo estado: {}", id, request.getEstado());
        
        SolicitudArmadoResponse updated = solicitudService.actualizarEstado(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/confirmar-armado")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Confirmar armado y descontar stock",
               description = "Solo ADMIN/OPERADOR. Descuenta el stock de componentes y marca como COMPLETADO")
    public ResponseEntity<SolicitudArmadoResponse> confirmarArmado(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id) {
        log.debug("POST /api/solicitudes-armado/{}/confirmar-armado", id);
        
        SolicitudArmadoResponse updated = solicitudService.confirmarArmado(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar solicitud de armado",
               description = "Solo ADMIN puede eliminar solicitudes")
    public ResponseEntity<MessageResponse> deleteSolicitud(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id) {
        log.debug("DELETE /api/solicitudes-armado/{}", id);
        
        solicitudService.delete(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Solicitud de armado eliminada exitosamente")
                .build());
    }
}
