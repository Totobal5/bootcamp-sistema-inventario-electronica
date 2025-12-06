package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ActualizarEstadoRequest;
import com.bootcamp.inventario.dto.request.SolicitudMecanizadoRequest;
import com.bootcamp.inventario.dto.response.SolicitudMecanizadoResponse;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ISolicitudMecanizadoService {
    
    /**
     * Obtiene todas las solicitudes de mecanizado
     */
    List<SolicitudMecanizadoResponse> findAll();
    
    /**
     * Obtiene una solicitud por ID
     */
    SolicitudMecanizadoResponse findById(Long id);
    
    /**
     * Obtiene solicitudes por estado
     */
    List<SolicitudMecanizadoResponse> findByEstado(EstadoSolicitud estado);
    
    /**
     * Obtiene solicitudes de un cliente específico
     */
    List<SolicitudMecanizadoResponse> findByClienteId(Long clienteId);
    
    /**
     * Busca solicitudes por nombre de placa
     */
    List<SolicitudMecanizadoResponse> searchByNombrePlaca(String nombrePlaca);
    
    /**
     * Crea una nueva solicitud de mecanizado (solo CLIENTE)
     */
    SolicitudMecanizadoResponse create(SolicitudMecanizadoRequest request, String username);
    
    /**
     * Actualiza una solicitud existente
     */
    SolicitudMecanizadoResponse update(Long id, SolicitudMecanizadoRequest request, String username);
    
    /**
     * Actualiza el estado de una solicitud (solo OPERADOR/ADMIN)
     */
    SolicitudMecanizadoResponse actualizarEstado(Long id, ActualizarEstadoRequest request);
    
    /**
     * Sube o actualiza el archivo Gerber de una solicitud
     * Incrementa automáticamente la versión del Gerber
     */
    SolicitudMecanizadoResponse uploadArchivoGerber(Long id, MultipartFile file, String username) throws IOException;
    
    /**
     * Elimina una solicitud (solo ADMIN)
     */
    void delete(Long id);
    
    /**
     * Verifica si un usuario es propietario de una solicitud
     */
    boolean isOwner(Long solicitudId, String username);
}
