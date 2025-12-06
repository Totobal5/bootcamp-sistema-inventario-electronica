package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.TareaRequest;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.dto.response.TareaResponse;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import com.bootcamp.inventario.service.ITareaService;
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
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tareas", description = "Gestión de tareas de armado asignadas a operadores")
public class TareaController {

    private final ITareaService tareaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Obtener todas las tareas",
               description = "ADMIN ve todas, OPERADOR solo las asignadas a él")
    public ResponseEntity<List<TareaResponse>> getAllTareas(Authentication authentication) {
        log.debug("GET /api/tareas - Usuario: {}", authentication.getName());
        
        // Si es OPERADOR, solo mostrar sus tareas asignadas
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OPERADOR"))) {
            List<TareaResponse> tareas = 
                    tareaService.findAll().stream()
                            .filter(t -> t.getOperadorUsername().equals(authentication.getName()))
                            .toList();
            return ResponseEntity.ok(tareas);
        }
        
        // ADMIN ve todas
        return ResponseEntity.ok(tareaService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Obtener tarea por ID",
               description = "ADMIN puede ver cualquiera, OPERADOR solo las asignadas a él")
    public ResponseEntity<TareaResponse> getTareaById(
            @PathVariable @Parameter(description = "ID de la tarea") Long id,
            Authentication authentication) {
        log.debug("GET /api/tareas/{} - Usuario: {}", id, authentication.getName());
        
        TareaResponse tarea = tareaService.findById(id);
        
        // Si es OPERADOR, verificar que sea su tarea asignada
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OPERADOR"))) {
            if (!tarea.getOperadorUsername().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        return ResponseEntity.ok(tarea);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Obtener tareas por estado")
    public ResponseEntity<List<TareaResponse>> getTareasByEstado(
            @PathVariable @Parameter(description = "Estado de la tarea") EstadoSolicitud estado,
            Authentication authentication) {
        log.debug("GET /api/tareas/estado/{}", estado);
        
        List<TareaResponse> tareas = tareaService.findByEstado(estado);
        
        // Si es OPERADOR, filtrar solo sus tareas
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OPERADOR"))) {
            tareas = tareas.stream()
                    .filter(t -> t.getOperadorUsername().equals(authentication.getName()))
                    .toList();
        }
        
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/solicitud/{solicitudId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Obtener tareas de una solicitud de armado")
    public ResponseEntity<List<TareaResponse>> getTareasBySolicitud(
            @PathVariable @Parameter(description = "ID de la solicitud") Long solicitudId) {
        log.debug("GET /api/tareas/solicitud/{}", solicitudId);
        return ResponseEntity.ok(tareaService.findBySolicitudArmadoId(solicitudId));
    }

    @GetMapping("/mis-tareas")
    @PreAuthorize("hasRole('OPERADOR')")
    @Operation(summary = "Obtener tareas del operador autenticado",
               description = "Retorna solo las tareas asignadas al operador que hace la petición")
    public ResponseEntity<List<TareaResponse>> getMisTareas(Authentication authentication) {
        log.debug("GET /api/tareas/mis-tareas - Usuario: {}", authentication.getName());
        
        List<TareaResponse> tareas = tareaService.findAll().stream()
                .filter(t -> t.getOperadorUsername().equals(authentication.getName()))
                .toList();
        
        return ResponseEntity.ok(tareas);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Crear nueva tarea",
               description = "ADMIN/OPERADOR pueden crear tareas y asignarlas a operadores")
    public ResponseEntity<TareaResponse> createTarea(@Valid @RequestBody TareaRequest request) {
        log.debug("POST /api/tareas");
        
        TareaResponse created = tareaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Actualizar tarea",
               description = "Solo se pueden actualizar tareas en estado PENDIENTE")
    public ResponseEntity<TareaResponse> updateTarea(
            @PathVariable @Parameter(description = "ID de la tarea") Long id,
            @Valid @RequestBody TareaRequest request) {
        log.debug("PUT /api/tareas/{}", id);
        
        TareaResponse updated = tareaService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Iniciar tarea",
               description = "Cambia estado a EN_PROCESO y registra fecha de inicio. Solo el operador asignado o ADMIN")
    public ResponseEntity<TareaResponse> iniciarTarea(
            @PathVariable @Parameter(description = "ID de la tarea") Long id,
            Authentication authentication) {
        log.debug("POST /api/tareas/{}/iniciar - Usuario: {}", id, authentication.getName());
        
        TareaResponse updated = tareaService.iniciar(id, authentication.getName());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/pausar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Pausar tarea",
               description = "Cambia estado a PAUSADO. Solo el operador asignado o ADMIN")
    public ResponseEntity<TareaResponse> pausarTarea(
            @PathVariable @Parameter(description = "ID de la tarea") Long id,
            @RequestParam(required = false) @Parameter(description = "Observaciones") String observaciones,
            Authentication authentication) {
        log.debug("POST /api/tareas/{}/pausar - Usuario: {}", id, authentication.getName());
        
        TareaResponse updated = tareaService.pausar(id, authentication.getName(), observaciones);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Completar tarea",
               description = "Cambia estado a COMPLETADO y registra fecha de fin. Solo el operador asignado o ADMIN")
    public ResponseEntity<TareaResponse> completarTarea(
            @PathVariable @Parameter(description = "ID de la tarea") Long id,
            @RequestParam(required = false) @Parameter(description = "Observaciones") String observaciones,
            Authentication authentication) {
        log.debug("POST /api/tareas/{}/completar - Usuario: {}", id, authentication.getName());
        
        TareaResponse updated = tareaService.completar(id, authentication.getName(), observaciones);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar tarea",
               description = "Solo ADMIN puede eliminar tareas")
    public ResponseEntity<MessageResponse> deleteTarea(
            @PathVariable @Parameter(description = "ID de la tarea") Long id) {
        log.debug("DELETE /api/tareas/{}", id);
        
        tareaService.delete(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Tarea eliminada exitosamente")
                .build());
    }
}
