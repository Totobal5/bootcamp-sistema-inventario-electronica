package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ActualizarEstadoRequest;
import com.bootcamp.inventario.dto.request.SolicitudMecanizadoRequest;
import com.bootcamp.inventario.dto.response.SolicitudMecanizadoResponse;
import com.bootcamp.inventario.exception.BadRequestException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.exception.UnauthorizedException;
import com.bootcamp.inventario.model.SolicitudMecanizado;
import com.bootcamp.inventario.model.Usuario;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import com.bootcamp.inventario.repository.SolicitudMecanizadoRepository;
import com.bootcamp.inventario.repository.UsuarioRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudMecanizadoServiceImpl implements ISolicitudMecanizadoService {

    private final SolicitudMecanizadoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Value("${file.upload-dir:uploads/gerber}")
    private String uploadDir;

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudMecanizadoResponse> findAll() {
        log.debug("Obteniendo todas las solicitudes de mecanizado");
        return solicitudRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudMecanizadoResponse findById(Long id) {
        log.debug("Buscando solicitud de mecanizado con ID: {}", id);
        SolicitudMecanizado solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Mecanizado", "id", id));
        return mapToResponse(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudMecanizadoResponse> findByEstado(EstadoSolicitud estado) {
        log.debug("Buscando solicitudes por estado: {}", estado);
        return solicitudRepository.findByEstado(estado).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudMecanizadoResponse> findByClienteId(Long clienteId) {
        log.debug("Buscando solicitudes del cliente: {}", clienteId);
        return solicitudRepository.findByClienteId(clienteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudMecanizadoResponse> searchByNombrePlaca(String nombrePlaca) {
        log.debug("Buscando solicitudes por nombre de placa: {}", nombrePlaca);
        return solicitudRepository.findByNombrePlacaContainingIgnoreCase(nombrePlaca).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SolicitudMecanizadoResponse create(SolicitudMecanizadoRequest request, String username) {
        log.debug("Creando nueva solicitud de mecanizado para usuario: {}", username);
        
        // Buscar el usuario cliente
        Usuario cliente = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        
        // Crear la solicitud
        SolicitudMecanizado solicitud = SolicitudMecanizado.builder()
                .nombrePlaca(request.getNombrePlaca())
                .descripcion(request.getDescripcion())
                .cantidad(request.getCantidad())
                .precio(request.getPrecio())
                .especificacionesTecnicas(request.getEspecificacionesTecnicas())
                .estado(EstadoSolicitud.SOLICITADO)
                .versionGerber(0) // Sin archivo Gerber inicialmente
                .cliente(cliente)
                .build();
        
        SolicitudMecanizado saved = solicitudRepository.save(solicitud);
        log.info("Solicitud de mecanizado creada exitosamente con ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SolicitudMecanizadoResponse update(Long id, SolicitudMecanizadoRequest request, String username) {
        log.debug("Actualizando solicitud de mecanizado con ID: {}", id);
        
        SolicitudMecanizado solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Mecanizado", "id", id));
        
        // Verificar que el usuario es el propietario
        if (!solicitud.getCliente().getUsername().equals(username)) {
            log.warn("Usuario {} intentó actualizar solicitud que no le pertenece: {}", username, id);
            throw new UnauthorizedException("No tienes permiso para actualizar esta solicitud");
        }
        
        // Solo se puede actualizar si está en estado SOLICITADO
        if (solicitud.getEstado() != EstadoSolicitud.SOLICITADO) {
            throw new BadRequestException("Solo se pueden actualizar solicitudes en estado SOLICITADO");
        }
        
        // Actualizar datos
        solicitud.setNombrePlaca(request.getNombrePlaca());
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setCantidad(request.getCantidad());
        solicitud.setPrecio(request.getPrecio());
        solicitud.setEspecificacionesTecnicas(request.getEspecificacionesTecnicas());
        
        SolicitudMecanizado updated = solicitudRepository.save(solicitud);
        log.info("Solicitud de mecanizado actualizada exitosamente: {}", id);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SolicitudMecanizadoResponse actualizarEstado(Long id, ActualizarEstadoRequest request) {
        log.debug("Actualizando estado de solicitud {} a {}", id, request.getEstado());
        
        SolicitudMecanizado solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Mecanizado", "id", id));
        
        // Validar transición de estado
        validateEstadoTransition(solicitud.getEstado(), request.getEstado());
        
        solicitud.setEstado(request.getEstado());
        
        SolicitudMecanizado updated = solicitudRepository.save(solicitud);
        log.info("Estado de solicitud {} actualizado a {}", id, request.getEstado());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SolicitudMecanizadoResponse uploadArchivoGerber(Long id, MultipartFile file, String username) throws IOException {
        log.debug("Subiendo archivo Gerber para solicitud: {}", id);
        
        SolicitudMecanizado solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Mecanizado", "id", id));
        
        // Verificar que el usuario es el propietario
        if (!solicitud.getCliente().getUsername().equals(username)) {
            log.warn("Usuario {} intentó subir archivo a solicitud que no le pertenece: {}", username, id);
            throw new UnauthorizedException("No tienes permiso para subir archivos a esta solicitud");
        }
        
        // Validar el archivo
        if (file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }
        
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Nombre de archivo inválido: " + originalFilename);
        }
        
        // Incrementar versión del Gerber
        int nuevaVersion = solicitud.getVersionGerber() + 1;
        
        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generar nombre único: solicitud_{id}_v{version}_{originalName}
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = String.format("solicitud_%d_v%d%s", id, nuevaVersion, extension);
        Path filePath = uploadPath.resolve(filename);
        
        // Guardar archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Actualizar solicitud con el método de la entidad
        solicitud.actualizarGerber("/" + uploadDir + "/" + filename);
        
        SolicitudMecanizado updated = solicitudRepository.save(solicitud);
        log.info("Archivo Gerber v{} subido exitosamente para solicitud {}", nuevaVersion, id);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando solicitud de mecanizado con ID: {}", id);
        
        SolicitudMecanizado solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Mecanizado", "id", id));
        
        solicitudRepository.delete(solicitud);
        log.info("Solicitud de mecanizado eliminada: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(Long solicitudId, String username) {
        SolicitudMecanizado solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Mecanizado", "id", solicitudId));
        return solicitud.getCliente().getUsername().equals(username);
    }

    /**
     * Valida que la transición de estado sea válida
     */
    private void validateEstadoTransition(EstadoSolicitud estadoActual, EstadoSolicitud nuevoEstado) {
        // SOLICITADO -> EN_PROCESO, CANCELADO
        // EN_PROCESO -> PAUSADO, COMPLETADO, CANCELADO
        // PAUSADO -> EN_PROCESO, CANCELADO
        // COMPLETADO -> no permite cambios
        // CANCELADO -> no permite cambios
        
        if (estadoActual == EstadoSolicitud.COMPLETADO || estadoActual == EstadoSolicitud.CANCELADO) {
            throw new BadRequestException("No se puede cambiar el estado de una solicitud " + estadoActual);
        }
        
        if (estadoActual == nuevoEstado) {
            throw new BadRequestException("El nuevo estado es igual al estado actual");
        }
    }

    /**
     * Convierte entidad a DTO
     */
    private SolicitudMecanizadoResponse mapToResponse(SolicitudMecanizado solicitud) {
        return SolicitudMecanizadoResponse.builder()
                .id(solicitud.getId())
                .nombrePlaca(solicitud.getNombrePlaca())
                .descripcion(solicitud.getDescripcion())
                .cantidad(solicitud.getCantidad())
                .precio(solicitud.getPrecio())
                .especificacionesTecnicas(solicitud.getEspecificacionesTecnicas())
                .estado(solicitud.getEstado())
                .versionGerber(solicitud.getVersionGerber())
                .archivoGerberUrl(solicitud.getGerberFileUrl())
                .clienteId(solicitud.getCliente().getId())
                .clienteUsername(solicitud.getCliente().getUsername())
                .clienteEmail(solicitud.getCliente().getEmail())
                .fechaCreacion(solicitud.getCreatedAt())
                .fechaActualizacion(solicitud.getUpdatedAt())
                .build();
    }
}
