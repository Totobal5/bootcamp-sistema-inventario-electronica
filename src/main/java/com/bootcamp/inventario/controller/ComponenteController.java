package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.ComponenteRequest;
import com.bootcamp.inventario.dto.response.ComponenteResponse;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.service.IComponenteService;
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
 * Controlador REST para gestión de componentes electrónicos
 */
@RestController
@RequestMapping("/api/componentes")
@RequiredArgsConstructor
@Tag(name = "Componentes Electrónicos", description = "Gestión de inventario de componentes electrónicos")
public class ComponenteController {

    private final IComponenteService componenteService;

    @GetMapping
    @Operation(summary = "Listar todos los componentes", 
               description = "Obtiene la lista completa de componentes electrónicos")
    public ResponseEntity<List<ComponenteResponse>> getAllComponentes() {
        return ResponseEntity.ok(componenteService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener componente por ID", 
               description = "Obtiene los detalles de un componente específico")
    public ResponseEntity<ComponenteResponse> getComponenteById(@PathVariable Long id) {
        return ResponseEntity.ok(componenteService.findById(id));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar por categoría", 
               description = "Obtiene todos los componentes de una categoría específica")
    public ResponseEntity<List<ComponenteResponse>> getByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(componenteService.findByCategoria(categoria));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar por nombre", 
               description = "Busca componentes cuyo nombre contenga el texto especificado")
    public ResponseEntity<List<ComponenteResponse>> searchByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(componenteService.searchByNombre(nombre));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Crear componente", 
               description = "Crea un nuevo componente electrónico (requiere rol ADMIN o OPERADOR)")
    public ResponseEntity<ComponenteResponse> createComponente(@Valid @RequestBody ComponenteRequest request) {
        ComponenteResponse created = componenteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar componente", 
               description = "Actualiza un componente existente (requiere rol ADMIN o OPERADOR)")
    public ResponseEntity<ComponenteResponse> updateComponente(
            @PathVariable Long id,
            @Valid @RequestBody ComponenteRequest request) {
        return ResponseEntity.ok(componenteService.update(id, request));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar stock", 
               description = "Actualiza solo el stock de un componente (requiere rol ADMIN o OPERADOR)")
    public ResponseEntity<ComponenteResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(componenteService.updateStock(id, cantidad));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Eliminar componente", 
               description = "Elimina un componente del sistema (requiere rol ADMIN)")
    public ResponseEntity<MessageResponse> deleteComponente(@PathVariable Long id) {
        componenteService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Componente eliminado exitosamente"));
    }
}
