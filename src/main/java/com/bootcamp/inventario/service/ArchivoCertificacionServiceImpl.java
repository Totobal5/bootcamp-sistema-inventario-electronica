package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.response.ArchivoCertificacionResponse;
import com.bootcamp.inventario.exception.BadRequestException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.ArchivoCertificacion;
import com.bootcamp.inventario.model.SolicitudArmado;
import com.bootcamp.inventario.model.enums.TipoArchivo;
import com.bootcamp.inventario.repository.ArchivoCertificacionRepository;
import com.bootcamp.inventario.repository.SolicitudArmadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchivoCertificacionServiceImpl implements IArchivoCertificacionService {

    private final ArchivoCertificacionRepository archivoRepository;
    private final SolicitudArmadoRepository solicitudArmadoRepository;
    
    @Value("${file.upload-dir:uploads/certificaciones}")
    private String uploadDir;
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "png", "jpg", "jpeg");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    @Transactional(readOnly = true)
    public List<ArchivoCertificacionResponse> findAll() {
        log.debug("Obteniendo todos los archivos de certificación");
        return archivoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoCertificacionResponse findById(Long id) {
        log.debug("Buscando archivo de certificación con ID: {}", id);
        ArchivoCertificacion archivo = archivoRepository.findByIdWithSolicitud(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo de Certificación", "id", id));
        return mapToResponse(archivo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivoCertificacionResponse> findBySolicitudArmadoId(Long solicitudId) {
        log.debug("Buscando archivos de la solicitud: {}", solicitudId);
        return archivoRepository.findBySolicitudArmadoId(solicitudId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivoCertificacionResponse> findByTipoArchivo(TipoArchivo tipoArchivo) {
        log.debug("Buscando archivos por tipo: {}", tipoArchivo);
        return archivoRepository.findByTipoArchivo(tipoArchivo).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ArchivoCertificacionResponse upload(
            Long solicitudArmadoId,
            TipoArchivo tipoArchivo,
            MultipartFile file
    ) throws IOException {
        log.debug("Subiendo archivo de certificación para solicitud: {}", solicitudArmadoId);
        
        // Buscar la solicitud de armado
        SolicitudArmado solicitud = solicitudArmadoRepository.findByIdWithRelations(solicitudArmadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", solicitudArmadoId));
        
        // Validar el archivo
        validateFile(file);
        
        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generar nombre único para el archivo
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = String.format(
            "cert_%s_%s_%s.%s",
            solicitudArmadoId,
            tipoArchivo.name().toLowerCase(),
            UUID.randomUUID().toString().substring(0, 8),
            extension
        );
        
        Path filePath = uploadPath.resolve(uniqueFilename);
        
        // Guardar archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Crear entidad
        ArchivoCertificacion archivo = ArchivoCertificacion.builder()
                .nombreArchivo(originalFilename)
                .tipoArchivo(tipoArchivo)
                .tamanoBytes(file.getSize())
                .archivoUrl("/" + uploadDir + "/" + uniqueFilename)
                .solicitudArmado(solicitud)
                .build();
        
        ArchivoCertificacion saved = archivoRepository.save(archivo);
        log.info("Archivo de certificación subido exitosamente: {} (ID: {})", originalFilename, saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando archivo de certificación con ID: {}", id);
        
        ArchivoCertificacion archivo = archivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo de Certificación", "id", id));
        
        // Intentar eliminar el archivo físico
        try {
            String filename = archivo.getArchivoUrl().substring(archivo.getArchivoUrl().lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("Archivo físico eliminado: {}", filename);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo físico: {}", e.getMessage());
        }
        
        archivoRepository.delete(archivo);
        log.info("Archivo de certificación eliminado: {}", id);
    }

    /**
     * Valida el archivo subido
     */
    private void validateFile(MultipartFile file) {
        // Validar que no esté vacío
        if (file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }
        
        // Validar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("El archivo excede el tamaño máximo permitido de 10MB");
        }
        
        // Validar nombre
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Nombre de archivo inválido: " + originalFilename);
        }
        
        // Validar extensión
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException(
                "Tipo de archivo no permitido. Solo se aceptan: " + String.join(", ", ALLOWED_EXTENSIONS)
            );
        }
    }

    /**
     * Obtiene la extensión del archivo
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BadRequestException("El archivo debe tener una extensión válida");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Convierte entidad a DTO
     */
    private ArchivoCertificacionResponse mapToResponse(ArchivoCertificacion archivo) {
        return ArchivoCertificacionResponse.builder()
                .id(archivo.getId())
                .nombreArchivo(archivo.getNombreArchivo())
                .tipoArchivo(archivo.getTipoArchivo())
                .tamanoBytes(archivo.getTamanoBytes())
                .archivoUrl(archivo.getArchivoUrl())
                .solicitudArmadoId(archivo.getSolicitudArmado().getId())
                .placaNombre(archivo.getSolicitudArmado().getPlaca().getNombre())
                .cantidadPlacas(archivo.getSolicitudArmado().getCantidad())
                .fechaCreacion(archivo.getFechaCreacion())
                .build();
    }
}
