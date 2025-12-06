package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.BomImportRequest;
import com.bootcamp.inventario.dto.request.PcbRevisionRequest;
import com.bootcamp.inventario.dto.response.BomImportResponse;
import com.bootcamp.inventario.dto.response.PcbRevisionResponse;
import com.bootcamp.inventario.service.IPcbRevisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * Controlador REST para gestión de revisiones de PCB
 */
@RestController
@RequestMapping("/api/pcb-revisions")
@RequiredArgsConstructor
@Tag(name = "PCB Revisions", description = "Gestión de revisiones de diseños PCB")
public class PcbRevisionController {

    private final IPcbRevisionService pcbRevisionService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener revisión por ID",
               description = "Retorna información detallada de una revisión específica")
    public ResponseEntity<PcbRevisionResponse> getById(
            @Parameter(description = "ID de la revisión") @PathVariable Long id) {
        return ResponseEntity.ok(pcbRevisionService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Actualizar revisión",
               description = "Actualiza especificaciones técnicas de una revisión (solo si no está aprobada)")
    public ResponseEntity<PcbRevisionResponse> update(
            @Parameter(description = "ID de la revisión") @PathVariable Long id,
            @Valid @RequestBody PcbRevisionRequest request,
            Principal principal) {
        return ResponseEntity.ok(pcbRevisionService.update(id, request, principal.getName()));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Aprobar revisión",
               description = "Aprueba formalmente la revisión y la bloquea para evitar modificaciones")
    public ResponseEntity<PcbRevisionResponse> approve(
            @Parameter(description = "ID de la revisión") @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(pcbRevisionService.approve(id, principal.getName()));
    }

    @PostMapping("/{id}/import-bom")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Importar BOM desde CSV",
               description = "Importa un Bill of Materials desde CSV y matchea automáticamente con el inventario por MPN")
    public ResponseEntity<BomImportResponse> importBom(
            @Parameter(description = "ID de la revisión") @PathVariable Long id,
            @Valid @RequestBody BomImportRequest request,
            Principal principal) {
        return ResponseEntity.ok(pcbRevisionService.importBom(id, request, principal.getName()));
    }

    @PostMapping("/{id}/promote-to-production")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Promover a producción",
               description = "Cambia el estado de la revisión a PRODUCTION (solo revisiones aprobadas)")
    public ResponseEntity<PcbRevisionResponse> promoteToProduction(
            @Parameter(description = "ID de la revisión") @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(pcbRevisionService.promoteToProduction(id, principal.getName()));
    }

    @PostMapping("/{id}/deprecate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deprecar revisión",
               description = "Marca la revisión como DEPRECATED para evitar su uso")
    public ResponseEntity<PcbRevisionResponse> deprecate(
            @Parameter(description = "ID de la revisión") @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(pcbRevisionService.deprecate(id, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar revisión",
               description = "Elimina una revisión (solo si no está aprobada)")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la revisión") @PathVariable Long id,
            Principal principal) {
        pcbRevisionService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
