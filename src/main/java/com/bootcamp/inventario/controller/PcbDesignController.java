package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.PcbDesignRequest;
import com.bootcamp.inventario.dto.request.PcbRevisionRequest;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.dto.response.PcbDesignResponse;
import com.bootcamp.inventario.dto.response.PcbRevisionResponse;
import com.bootcamp.inventario.service.IPcbDesignService;
import com.bootcamp.inventario.service.IPcbRevisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controlador REST para gestión de diseños de PCB
 */
@RestController
@RequestMapping("/api/pcb-designs")
@RequiredArgsConstructor
@Tag(name = "PCB Designs", description = "Gestión de diseños de PCB y sus revisiones")
public class PcbDesignController {

    private final IPcbDesignService pcbDesignService;
    private final IPcbRevisionService pcbRevisionService;

    @GetMapping
    @Operation(summary = "Listar todos los diseños de PCB",
            description = "Obtiene la lista completa de diseños de PCB con sus componentes")
    public ResponseEntity<List<PcbDesignResponse>> getAllPcbDesigns() {
        return ResponseEntity.ok(pcbDesignService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener diseño de PCB por ID",
            description = "Obtiene los detalles de un diseño de PCB específico con su BOM")
    public ResponseEntity<PcbDesignResponse> getPcbDesignById(@PathVariable Long id) {
        return ResponseEntity.ok(pcbDesignService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar diseños de PCB por nombre",
            description = "Busca diseños de PCB cuyo nombre contenga el texto especificado")
    public ResponseEntity<List<PcbDesignResponse>> searchByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(pcbDesignService.searchByNombre(nombre));
    }

    @GetMapping("/{id}/verificar-stock")
    @Operation(summary = "Verificar disponibilidad de stock",
            description = "Verifica si hay suficiente stock de componentes para fabricar el diseño de PCB")
    public ResponseEntity<Boolean> verificarStock(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer cantidad) {
        return ResponseEntity.ok(pcbDesignService.verificarDisponibilidadStock(id, cantidad));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Crear diseño de PCB",
            description = "Crea un nuevo diseño de PCB con su BOM (requiere rol ADMIN o OPERADOR)")
    public ResponseEntity<PcbDesignResponse> createPcbDesign(@Valid @RequestBody PcbDesignRequest request) {
        PcbDesignResponse created = pcbDesignService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar diseño de PCB",
            description = "Actualiza un diseño de PCB existente y su BOM (requiere rol ADMIN o OPERADOR)")
    public ResponseEntity<PcbDesignResponse> updatePcbDesign(
            @PathVariable Long id,
            @Valid @RequestBody PcbDesignRequest request) {
        return ResponseEntity.ok(pcbDesignService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Eliminar diseño de PCB",
            description = "Elimina un diseño de PCB del sistema (requiere rol ADMIN)")
    public ResponseEntity<MessageResponse> deletePcbDesign(@PathVariable Long id) {
        pcbDesignService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Diseño de PCB eliminado exitosamente"));
    }

    // ========== GESTIÓN DE REVISIONES ==========

    @GetMapping("/{id}/revisions")
    @Operation(summary = "Listar revisiones de un diseño",
               description = "Obtiene todas las revisiones de un diseño de PCB ordenadas por fecha")
    public ResponseEntity<List<PcbRevisionResponse>> getRevisions(
            @Parameter(description = "ID del diseño de PCB") @PathVariable Long id) {
        return ResponseEntity.ok(pcbRevisionService.getByPcbDesignId(id));
    }

    @PostMapping("/{id}/revisions")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Crear nueva revisión",
               description = "Crea una nueva revisión para el diseño de PCB (ej: v1.0, v1.1, rev-A)")
    public ResponseEntity<PcbRevisionResponse> createRevision(
            @Parameter(description = "ID del diseño de PCB") @PathVariable Long id,
            @Valid @RequestBody PcbRevisionRequest request,
            Principal principal) {
        PcbRevisionResponse created = pcbRevisionService.createRevision(id, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
