package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ActualizarEstadoRequest;
import com.bootcamp.inventario.dto.request.SolicitudArmadoRequest;
import com.bootcamp.inventario.dto.response.SolicitudArmadoResponse;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;

import java.util.List;

public interface ISolicitudArmadoService {
    
    /**
     * Obtiene todas las solicitudes de armado
     */
    List<SolicitudArmadoResponse> findAll();
    
    /**
     * Obtiene una solicitud por ID
     */
    SolicitudArmadoResponse findById(Long id);
    
    /**
     * Obtiene solicitudes por estado
     */
    List<SolicitudArmadoResponse> findByEstado(EstadoSolicitud estado);
    
    /**
     * Obtiene solicitudes de un cliente específico
     */
    List<SolicitudArmadoResponse> findByClienteId(Long clienteId);
    
    /**
     * Obtiene solicitudes de una placa específica
     */
    List<SolicitudArmadoResponse> findByPlacaId(Long placaId);
    
    /**
     * Crea una nueva solicitud de armado (solo CLIENTE)
     * Valida que haya stock suficiente de componentes
     */
    SolicitudArmadoResponse create(SolicitudArmadoRequest request, String username);
    
    /**
     * Actualiza el estado de una solicitud (solo OPERADOR/ADMIN)
     */
    SolicitudArmadoResponse actualizarEstado(Long id, ActualizarEstadoRequest request);
    
    /**
     * Confirma el armado y descuenta el stock de componentes (solo OPERADOR/ADMIN)
     * Solo se puede confirmar si el estado es EN_PROCESO
     */
    SolicitudArmadoResponse confirmarArmado(Long id);
    
    /**
     * Elimina una solicitud (solo ADMIN)
     */
    void delete(Long id);
    
    /**
     * Verifica si un usuario es propietario de una solicitud
     */
    boolean isOwner(Long solicitudId, String username);
}
