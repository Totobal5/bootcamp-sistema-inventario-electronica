package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.ActualizarEstadoRequest;
import com.bootcamp.inventario.dto.request.SolicitudMecanizadoRequest;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.dto.response.SolicitudMecanizadoResponse;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import com.bootcamp.inventario.service.ISolicitudMecanizadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-mecanizado")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Solicitudes de Mecanizado", description = "Gestión de solicitudes de mecanizado de placas PCB")
public class SolicitudMecanizadoController {

    private final ISolicitudMecanizadoService solicitudService;

    @GetMapping
    @Operation(summary = "Obtener todas las solicitudes de mecanizado",
               description = "ADMIN/OPERADOR ven todas, CLIENTE solo las propias")
    public ResponseEntity<List<SolicitudMecanizadoResponse>> getAllSolicitudes(Authentication authentication) {
        log.debug("GET /api/solicitudes-mecanizado - Usuario: {}", authentication.getName());
        
        // Si es CLIENTE, solo mostrar sus propias solicitudes
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENTE"))) {
            List<SolicitudMecanizadoResponse> solicitudes = 
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
    public ResponseEntity<SolicitudMecanizadoResponse> getSolicitudById(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id,
            Authentication authentication) {
        log.debug("GET /api/solicitudes-mecanizado/{} - Usuario: {}", id, authentication.getName());
        
        SolicitudMecanizadoResponse solicitud = solicitudService.findById(id);
        
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
    public ResponseEntity<List<SolicitudMecanizadoResponse>> getSolicitudesByEstado(
            @PathVariable @Parameter(description = "Estado de la solicitud") EstadoSolicitud estado) {
        log.debug("GET /api/solicitudes-mecanizado/estado/{}", estado);
        return ResponseEntity.ok(solicitudService.findByEstado(estado));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar solicitudes por nombre de placa")
    public ResponseEntity<List<SolicitudMecanizadoResponse>> searchSolicitudes(
            @RequestParam @Parameter(description = "Nombre de la placa") String nombrePlaca,
            Authentication authentication) {
        log.debug("GET /api/solicitudes-mecanizado/search?nombrePlaca={}", nombrePlaca);
        
        List<SolicitudMecanizadoResponse> solicitudes = solicitudService.searchByNombrePlaca(nombrePlaca);
        
        // Si es CLIENTE, filtrar solo las propias
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENTE"))) {
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getClienteUsername().equals(authentication.getName()))
                    .toList();
        }
        
        return ResponseEntity.ok(solicitudes);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear nueva solicitud de mecanizado",
               description = "Solo CLIENTE puede crear solicitudes")
    public ResponseEntity<SolicitudMecanizadoResponse> createSolicitud(
            @Valid @RequestBody SolicitudMecanizadoRequest request,
            Authentication authentication) {
        log.debug("POST /api/solicitudes-mecanizado - Usuario: {}", authentication.getName());
        
        SolicitudMecanizadoResponse created = solicitudService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Actualizar solicitud de mecanizado",
               description = "Solo el CLIENTE propietario puede actualizar (solo en estado SOLICITADO)")
    public ResponseEntity<SolicitudMecanizadoResponse> updateSolicitud(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id,
            @Valid @RequestBody SolicitudMecanizadoRequest request,
            Authentication authentication) {
        log.debug("PUT /api/solicitudes-mecanizado/{} - Usuario: {}", id, authentication.getName());
        
        SolicitudMecanizadoResponse updated = solicitudService.update(id, request, authentication.getName());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Actualizar estado de la solicitud",
               description = "Solo ADMIN/OPERADOR pueden cambiar estados")
    public ResponseEntity<SolicitudMecanizadoResponse> actualizarEstado(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        log.debug("PATCH /api/solicitudes-mecanizado/{}/estado - Nuevo estado: {}", id, request.getEstado());
        
        SolicitudMecanizadoResponse updated = solicitudService.actualizarEstado(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping(value = "/{id}/upload-gerber", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Subir o actualizar archivo Gerber",
               description = "Solo el CLIENTE propietario puede subir archivos. Versión se incrementa automáticamente")
    public ResponseEntity<SolicitudMecanizadoResponse> uploadArchivoGerber(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id,
            @RequestParam("file") @Parameter(description = "Archivo Gerber (ZIP)") MultipartFile file,
            Authentication authentication) throws IOException {
        log.debug("POST /api/solicitudes-mecanizado/{}/upload-gerber - Usuario: {}", id, authentication.getName());
        
        SolicitudMecanizadoResponse updated = solicitudService.uploadArchivoGerber(id, file, authentication.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar solicitud de mecanizado",
               description = "Solo ADMIN puede eliminar solicitudes")
    public ResponseEntity<MessageResponse> deleteSolicitud(
            @PathVariable @Parameter(description = "ID de la solicitud") Long id) {
        log.debug("DELETE /api/solicitudes-mecanizado/{}", id);
        
        solicitudService.delete(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Solicitud de mecanizado eliminada exitosamente")
                .build());
    }
}
