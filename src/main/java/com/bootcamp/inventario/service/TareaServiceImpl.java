package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.TareaRequest;
import com.bootcamp.inventario.dto.response.TareaResponse;
import com.bootcamp.inventario.exception.BadRequestException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.exception.UnauthorizedException;
import com.bootcamp.inventario.model.SolicitudArmado;
import com.bootcamp.inventario.model.Tarea;
import com.bootcamp.inventario.model.Usuario;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import com.bootcamp.inventario.model.enums.Rol;
import com.bootcamp.inventario.repository.SolicitudArmadoRepository;
import com.bootcamp.inventario.repository.TareaRepository;
import com.bootcamp.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareaServiceImpl implements ITareaService {

    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudArmadoRepository solicitudArmadoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TareaResponse> findAll() {
        log.debug("Obteniendo todas las tareas");
        return tareaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TareaResponse findById(Long id) {
        log.debug("Buscando tarea con ID: {}", id);
        Tarea tarea = tareaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        return mapToResponse(tarea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaResponse> findByEstado(EstadoSolicitud estado) {
        log.debug("Buscando tareas por estado: {}", estado);
        return tareaRepository.findByEstado(estado).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaResponse> findByOperadorId(Long operadorId) {
        log.debug("Buscando tareas del operador: {}", operadorId);
        return tareaRepository.findByOperadorIdWithSolicitud(operadorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaResponse> findBySolicitudArmadoId(Long solicitudId) {
        log.debug("Buscando tareas de la solicitud: {}", solicitudId);
        return tareaRepository.findBySolicitudArmadoId(solicitudId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TareaResponse create(TareaRequest request) {
        log.debug("Creando nueva tarea para solicitud: {}", request.getSolicitudArmadoId());
        
        // Buscar la solicitud de armado
        SolicitudArmado solicitud = solicitudArmadoRepository.findByIdWithRelations(request.getSolicitudArmadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", request.getSolicitudArmadoId()));
        
        // Buscar el operador
        Usuario operador = usuarioRepository.findById(request.getOperadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getOperadorId()));
        
        // Validar que sea un operador
        if (operador.getRol() != Rol.OPERADOR && operador.getRol() != Rol.ADMIN) {
            throw new BadRequestException("El usuario asignado debe tener rol OPERADOR o ADMIN");
        }
        
        // Crear la tarea
        Tarea tarea = Tarea.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .estado(EstadoSolicitud.PENDIENTE)
                .solicitudArmado(solicitud)
                .operador(operador)
                .build();
        
        Tarea saved = tareaRepository.save(tarea);
        log.info("Tarea creada exitosamente con ID: {} para operador: {}", saved.getId(), operador.getUsername());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TareaResponse update(Long id, TareaRequest request) {
        log.debug("Actualizando tarea con ID: {}", id);
        
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        // Solo se puede actualizar si está en estado PENDIENTE
        if (tarea.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new BadRequestException("Solo se pueden actualizar tareas en estado PENDIENTE");
        }
        
        // Buscar la solicitud de armado si cambió
        if (!tarea.getSolicitudArmado().getId().equals(request.getSolicitudArmadoId())) {
            SolicitudArmado solicitud = solicitudArmadoRepository.findById(request.getSolicitudArmadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", request.getSolicitudArmadoId()));
            tarea.setSolicitudArmado(solicitud);
        }
        
        // Buscar el operador si cambió
        if (!tarea.getOperador().getId().equals(request.getOperadorId())) {
            Usuario operador = usuarioRepository.findById(request.getOperadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getOperadorId()));
            
            if (operador.getRol() != Rol.OPERADOR && operador.getRol() != Rol.ADMIN) {
                throw new BadRequestException("El usuario asignado debe tener rol OPERADOR o ADMIN");
            }
            
            tarea.setOperador(operador);
        }
        
        tarea.setTitulo(request.getTitulo());
        tarea.setDescripcion(request.getDescripcion());
        
        Tarea updated = tareaRepository.save(tarea);
        log.info("Tarea actualizada exitosamente: {}", id);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public TareaResponse iniciar(Long id, String username) {
        log.debug("Iniciando tarea {} por usuario: {}", id, username);
        
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        // Verificar permisos (debe ser el operador asignado o ADMIN)
        validateOperadorPermission(tarea, username);
        
        // Usar el método de la entidad
        tarea.iniciar();
        
        Tarea updated = tareaRepository.save(tarea);
        log.info("Tarea {} iniciada por {}", id, username);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public TareaResponse pausar(Long id, String username, String observaciones) {
        log.debug("Pausando tarea {} por usuario: {}", id, username);
        
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        // Verificar permisos
        validateOperadorPermission(tarea, username);
        
        // Usar el método de la entidad
        tarea.pausar(observaciones);
        
        Tarea updated = tareaRepository.save(tarea);
        log.info("Tarea {} pausada por {}", id, username);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public TareaResponse completar(Long id, String username, String observaciones) {
        log.debug("Completando tarea {} por usuario: {}", id, username);
        
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        // Verificar permisos
        validateOperadorPermission(tarea, username);
        
        // Usar el método de la entidad
        tarea.completar(observaciones);
        
        Tarea updated = tareaRepository.save(tarea);
        log.info("Tarea {} completada por {}", id, username);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando tarea con ID: {}", id);
        
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        tareaRepository.delete(tarea);
        log.info("Tarea eliminada: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAssignedOperador(Long tareaId, String username) {
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", tareaId));
        return tarea.getOperador().getUsername().equals(username);
    }

    /**
     * Valida que el usuario tenga permiso para modificar la tarea
     * Debe ser el operador asignado o tener rol ADMIN
     */
    private void validateOperadorPermission(Tarea tarea, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        
        // Si es ADMIN, tiene permiso
        if (usuario.getRol() == Rol.ADMIN) {
            return;
        }
        
        // Si es el operador asignado, tiene permiso
        if (tarea.getOperador().getUsername().equals(username)) {
            return;
        }
        
        // De lo contrario, no tiene permiso
        throw new UnauthorizedException("No tienes permiso para modificar esta tarea");
    }

    /**
     * Convierte entidad a DTO
     */
    private TareaResponse mapToResponse(Tarea tarea) {
        return TareaResponse.builder()
                .id(tarea.getId())
                .titulo(tarea.getTitulo())
                .descripcion(tarea.getDescripcion())
                .estado(tarea.getEstado())
                .observaciones(tarea.getObservaciones())
                .solicitudArmadoId(tarea.getSolicitudArmado().getId())
                .placaNombre(tarea.getSolicitudArmado().getPlaca().getNombre())
                .cantidadPlacas(tarea.getSolicitudArmado().getCantidad())
                .operadorId(tarea.getOperador().getId())
                .operadorUsername(tarea.getOperador().getUsername())
                .operadorNombre(tarea.getOperador().getEmail()) // Podría ser nombre completo si existiera el campo
                .fechaInicio(tarea.getFechaInicio())
                .fechaFin(tarea.getFechaFin())
                .fechaCreacion(tarea.getCreatedAt())
                .fechaActualizacion(tarea.getUpdatedAt())
                .build();
    }
}
