package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ActualizarEstadoRequest;
import com.bootcamp.inventario.dto.request.SolicitudArmadoRequest;
import com.bootcamp.inventario.dto.response.SolicitudArmadoResponse;
import com.bootcamp.inventario.exception.BadRequestException;
import com.bootcamp.inventario.exception.InsufficientStockException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.*;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import com.bootcamp.inventario.repository.ComponenteElectronicoRepository;
import com.bootcamp.inventario.repository.PcbDesignRepository;
import com.bootcamp.inventario.repository.SolicitudArmadoRepository;
import com.bootcamp.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudArmadoServiceImpl implements ISolicitudArmadoService {

    private final SolicitudArmadoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final PcbDesignRepository pcbDesignRepository;
    private final ComponenteElectronicoRepository componenteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudArmadoResponse> findAll() {
        log.debug("Obteniendo todas las solicitudes de armado");
        return solicitudRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudArmadoResponse findById(Long id) {
        log.debug("Buscando solicitud de armado con ID: {}", id);
        SolicitudArmado solicitud = solicitudRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", id));
        return mapToResponse(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudArmadoResponse> findByEstado(EstadoSolicitud estado) {
        log.debug("Buscando solicitudes por estado: {}", estado);
        return solicitudRepository.findByEstado(estado).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudArmadoResponse> findByClienteId(Long clienteId) {
        log.debug("Buscando solicitudes del cliente: {}", clienteId);
        return solicitudRepository.findByClienteIdWithPlaca(clienteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudArmadoResponse> findByPcbDesignId(Long pcbDesignId) {
        log.debug("Buscando solicitudes del diseño PCB: {}", pcbDesignId);
        return solicitudRepository.findByPcbDesignId(pcbDesignId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SolicitudArmadoResponse create(SolicitudArmadoRequest request, String username) {
        log.debug("Creando nueva solicitud de armado para usuario: {}", username);
        
        // Buscar el usuario cliente
        Usuario cliente = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        
        // Buscar el diseño de PCB con sus componentes
        PcbDesign pcbDesign = pcbDesignRepository.findByIdWithComponentes(request.getPcbDesignId())
                .orElseThrow(() -> new ResourceNotFoundException("PcbDesign", "id", request.getPcbDesignId()));
        
        // Validar que haya stock suficiente
        boolean stockDisponible = verificarStockDisponible(pcbDesign, request.getCantidad());
        if (!stockDisponible) {
            throw new InsufficientStockException(
                "No hay stock suficiente de componentes para armar " + request.getCantidad() + 
                " unidades del diseño PCB '" + pcbDesign.getNombre() + "'"
            );
        }
        
        // Crear la solicitud
        SolicitudArmado solicitud = SolicitudArmado.builder()
                .pcbDesign(pcbDesign)
                .cantidad(request.getCantidad())
                .observaciones(request.getObservaciones())
                .estado(EstadoSolicitud.PENDIENTE)
                .cliente(cliente)
                .build();
        
        SolicitudArmado saved = solicitudRepository.save(solicitud);
        log.info("Solicitud de armado creada exitosamente con ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SolicitudArmadoResponse actualizarEstado(Long id, ActualizarEstadoRequest request) {
        log.debug("Actualizando estado de solicitud {} a {}", id, request.getEstado());
        
        SolicitudArmado solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", id));
        
        // Validar transición de estado
        validateEstadoTransition(solicitud.getEstado(), request.getEstado());
        
        solicitud.setEstado(request.getEstado());
        
        SolicitudArmado updated = solicitudRepository.save(solicitud);
        log.info("Estado de solicitud {} actualizado a {}", id, request.getEstado());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SolicitudArmadoResponse confirmarArmado(Long id) {
        log.debug("Confirmando armado de solicitud: {}", id);
        
        SolicitudArmado solicitud = solicitudRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", id));
        
        // Validar que esté en estado EN_PROCESO
        if (solicitud.getEstado() != EstadoSolicitud.EN_PROCESO) {
            throw new BadRequestException("Solo se pueden confirmar solicitudes en estado EN_PROCESO");
        }
        
        // Cargar componentes del diseño PCB
        PcbDesign pcbDesign = pcbDesignRepository.findByIdWithComponentes(solicitud.getPcbDesign().getId())
                .orElseThrow(() -> new ResourceNotFoundException("PcbDesign", "id", solicitud.getPcbDesign().getId()));
        
        // Verificar stock disponible nuevamente (por seguridad)
        boolean stockDisponible = verificarStockDisponible(pcbDesign, solicitud.getCantidad());
        if (!stockDisponible) {
            throw new InsufficientStockException(
                "No hay stock suficiente para confirmar el armado de " + solicitud.getCantidad() + 
                " unidades del diseño PCB '" + pcbDesign.getNombre() + "'"
            );
        }
        
        // Descontar stock de componentes
        for (BomEntry be : pcbDesign.getComponentes()) {
            ComponenteElectronico componente = be.getComponente();
            int stockNecesario = be.getQuantity() * solicitud.getCantidad();
            
            componente.setStockActual(componente.getStockActual() - stockNecesario);
            componenteRepository.save(componente);
            
            log.info("Stock del componente '{}' reducido en {} unidades (de {} a {})",
                    componente.getNombre(), stockNecesario, 
                    componente.getStockActual() + stockNecesario, componente.getStockActual());
        }
        
        // Cambiar estado a COMPLETADO
        solicitud.setEstado(EstadoSolicitud.COMPLETADO);
        SolicitudArmado updated = solicitudRepository.save(solicitud);
        
        log.info("Armado confirmado exitosamente para solicitud {}", id);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando solicitud de armado con ID: {}", id);
        
        SolicitudArmado solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", id));
        
        solicitudRepository.delete(solicitud);
        log.info("Solicitud de armado eliminada: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(Long solicitudId, String username) {
        SolicitudArmado solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Armado", "id", solicitudId));
        return solicitud.getCliente().getUsername().equals(username);
    }

    /**
     * Verifica si hay stock suficiente para armar la cantidad solicitada
     */
    private boolean verificarStockDisponible(PcbDesign pcbDesign, int cantidad) {
        for (BomEntry be : pcbDesign.getComponentes()) {
            int stockNecesario = be.getQuantity() * cantidad;
            int stockDisponible = be.getComponente().getStockActual();
            
            if (stockDisponible < stockNecesario) {
                log.warn("Stock insuficiente del componente '{}': necesario={}, disponible={}",
                        be.getComponente().getNombre(), stockNecesario, stockDisponible);
                return false;
            }
        }
        return true;
    }

    /**
     * Valida que la transición de estado sea válida
     */
    private void validateEstadoTransition(EstadoSolicitud estadoActual, EstadoSolicitud nuevoEstado) {
        // PENDIENTE -> EN_PROCESO, CANCELADO
        // EN_PROCESO -> PAUSADO, COMPLETADO (vía confirmarArmado), CANCELADO
        // PAUSADO -> EN_PROCESO, CANCELADO
        // COMPLETADO -> no permite cambios
        // CANCELADO -> no permite cambios
        
        if (estadoActual == EstadoSolicitud.COMPLETADO || estadoActual == EstadoSolicitud.CANCELADO) {
            throw new BadRequestException("No se puede cambiar el estado de una solicitud " + estadoActual);
        }
        
        if (estadoActual == nuevoEstado) {
            throw new BadRequestException("El nuevo estado es igual al estado actual");
        }
        
        // No se puede marcar como COMPLETADO directamente, debe usar confirmarArmado
        if (nuevoEstado == EstadoSolicitud.COMPLETADO) {
            throw new BadRequestException("Use el endpoint /confirmar-armado para completar la solicitud");
        }
    }

    /**
     * Convierte entidad a DTO
     */
    private SolicitudArmadoResponse mapToResponse(SolicitudArmado solicitud) {
        // Verificar stock disponible
        PcbDesign pcbDesign = solicitud.getPcbDesign();
        boolean stockDisponible = false;
        
        if (pcbDesign.getComponentes() != null && !pcbDesign.getComponentes().isEmpty()) {
            stockDisponible = verificarStockDisponible(pcbDesign, solicitud.getCantidad());
        }
        
        // Mapear certificaciones si existen
        List<SolicitudArmadoResponse.ArchivoCertificacionResponse> certificaciones = null;
        if (solicitud.getCertificaciones() != null && !solicitud.getCertificaciones().isEmpty()) {
            certificaciones = solicitud.getCertificaciones().stream()
                    .map(cert -> SolicitudArmadoResponse.ArchivoCertificacionResponse.builder()
                            .id(cert.getId())
                            .nombreArchivo(cert.getNombreArchivo())
                            .tipoArchivo(cert.getTipoArchivo().name())
                            .archivoUrl(cert.getArchivoUrl())
                            .build())
                    .collect(Collectors.toList());
        }
        
        return SolicitudArmadoResponse.builder()
                .id(solicitud.getId())
                .pcbDesignId(pcbDesign.getId())
                .pcbDesignNombre(pcbDesign.getNombre())
                .cantidad(solicitud.getCantidad())
                .observaciones(solicitud.getObservaciones())
                .estado(solicitud.getEstado())
                .stockDisponible(stockDisponible)
                .clienteId(solicitud.getCliente().getId())
                .clienteUsername(solicitud.getCliente().getUsername())
                .clienteEmail(solicitud.getCliente().getEmail())
                .certificaciones(certificaciones)
                .fechaCreacion(solicitud.getCreatedAt())
                .fechaActualizacion(solicitud.getUpdatedAt())
                .build();
    }
}
