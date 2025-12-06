package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.response.ArchivoCertificacionResponse;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.model.enums.TipoArchivo;
import com.bootcamp.inventario.service.IArchivoCertificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/certificaciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Certificaciones", description = "Gestión de archivos de certificación e inspección")
public class ArchivoCertificacionController {

    private final IArchivoCertificacionService archivoService;

    @GetMapping
    @Operation(summary = "Obtener todos los archivos de certificación",
               description = "Público - Todos pueden visualizar certificaciones")
    public ResponseEntity<List<ArchivoCertificacionResponse>> getAllArchivos() {
        log.debug("GET /api/certificaciones");
        return ResponseEntity.ok(archivoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener archivo por ID",
               description = "Público - Todos pueden visualizar")
    public ResponseEntity<ArchivoCertificacionResponse> getArchivoById(
            @PathVariable @Parameter(description = "ID del archivo") Long id) {
        log.debug("GET /api/certificaciones/{}", id);
        return ResponseEntity.ok(archivoService.findById(id));
    }

    @GetMapping("/solicitud/{solicitudId}")
    @Operation(summary = "Obtener archivos de una solicitud de armado",
               description = "Público - Todos pueden visualizar")
    public ResponseEntity<List<ArchivoCertificacionResponse>> getArchivosBySolicitud(
            @PathVariable @Parameter(description = "ID de la solicitud") Long solicitudId) {
        log.debug("GET /api/certificaciones/solicitud/{}", solicitudId);
        return ResponseEntity.ok(archivoService.findBySolicitudArmadoId(solicitudId));
    }

    @GetMapping("/tipo/{tipoArchivo}")
    @Operation(summary = "Obtener archivos por tipo",
               description = "Público - Todos pueden visualizar")
    public ResponseEntity<List<ArchivoCertificacionResponse>> getArchivosByTipo(
            @PathVariable @Parameter(description = "Tipo de archivo") TipoArchivo tipoArchivo) {
        log.debug("GET /api/certificaciones/tipo/{}", tipoArchivo);
        return ResponseEntity.ok(archivoService.findByTipoArchivo(tipoArchivo));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Subir archivo de certificación",
               description = "Solo ADMIN/OPERADOR. Tipos permitidos: PDF, PNG, JPG, JPEG. Máximo 10MB")
    public ResponseEntity<ArchivoCertificacionResponse> uploadArchivo(
            @RequestParam("solicitudArmadoId") @Parameter(description = "ID de la solicitud") Long solicitudArmadoId,
            @RequestParam("tipoArchivo") @Parameter(description = "Tipo de archivo (CERTIFICACION, INSPECCION)") TipoArchivo tipoArchivo,
            @RequestParam("file") @Parameter(description = "Archivo PDF o imagen") MultipartFile file
    ) throws IOException {
        log.debug("POST /api/certificaciones - Solicitud: {}, Tipo: {}", solicitudArmadoId, tipoArchivo);
        
        ArchivoCertificacionResponse uploaded = archivoService.upload(solicitudArmadoId, tipoArchivo, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar archivo de certificación",
               description = "Solo ADMIN puede eliminar archivos")
    public ResponseEntity<MessageResponse> deleteArchivo(
            @PathVariable @Parameter(description = "ID del archivo") Long id) {
        log.debug("DELETE /api/certificaciones/{}", id);
        
        archivoService.delete(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Archivo de certificación eliminado exitosamente")
                .build());
    }
}
