package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.PlacaRequest;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.dto.response.PlacaResponse;
import com.bootcamp.inventario.service.IPlacaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de placas
 */
@RestController
@RequestMapping("/api/placas")
@RequiredArgsConstructor
@Tag(name = "Placas", description = "Gestión de placas electrónicas y sus componentes")
public class PlacaController {

    private final IPlacaService placaService;

    @GetMapping
    @Operation(summary = "Listar todas las placas",
            description = "Obtiene la lista completa de placas con sus componentes")
    public ResponseEntity<List<PlacaResponse>> getAllPlacas() {
        return ResponseEntity.ok(placaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener placa por ID",
            description = "Obtiene los detalles de una placa específica con sus componentes")
    public ResponseEntity<PlacaResponse> getPlacaById(@PathVariable Long id) {
        return ResponseEntity.ok(placaService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar placas por nombre",
            description = "Busca placas cuyo nombre contenga el texto especificado")
    public ResponseEntity<List<PlacaResponse>> searchByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(placaService.searchByNombre(nombre));
    }

    @GetMapping("/{id}/verificar-stock")
    @Operation(summary = "Verificar disponibilidad de stock",
            description = "Verifica si hay suficiente stock de componentes para armar la placa")
    public ResponseEntity<Boolean> verificarStock(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer cantidad) {
        return ResponseEntity.ok(placaService.verificarDisponibilidadStock(id, cantidad));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Crear placa",
            description = "Crea una nueva placa con sus componentes (requiere rol ADMIN o OPERADOR)")
    public ResponseEntity<PlacaResponse> createPlaca(@Valid @RequestBody PlacaRequest request) {
        PlacaResponse created = placaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar placa",
            description = "Actualiza una placa existente y sus componentes (requiere rol ADMIN o OPERADOR)")
    public ResponseEntity<PlacaResponse> updatePlaca(
            @PathVariable Long id,
            @Valid @RequestBody PlacaRequest request) {
        return ResponseEntity.ok(placaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Eliminar placa",
            description = "Elimina una placa del sistema (requiere rol ADMIN)")
    public ResponseEntity<MessageResponse> deletePlaca(@PathVariable Long id) {
        placaService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Placa eliminada exitosamente"));
    }
}
