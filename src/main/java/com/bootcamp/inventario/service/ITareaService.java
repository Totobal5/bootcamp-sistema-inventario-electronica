package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.TareaRequest;
import com.bootcamp.inventario.dto.response.TareaResponse;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;

import java.util.List;

public interface ITareaService {
    
    /**
     * Obtiene todas las tareas
     */
    List<TareaResponse> findAll();
    
    /**
     * Obtiene una tarea por ID
     */
    TareaResponse findById(Long id);
    
    /**
     * Obtiene tareas por estado
     */
    List<TareaResponse> findByEstado(EstadoSolicitud estado);
    
    /**
     * Obtiene tareas de un operador específico
     */
    List<TareaResponse> findByOperadorId(Long operadorId);
    
    /**
     * Obtiene tareas de una solicitud de armado
     */
    List<TareaResponse> findBySolicitudArmadoId(Long solicitudId);
    
    /**
     * Crea una nueva tarea (solo ADMIN/OPERADOR)
     */
    TareaResponse create(TareaRequest request);
    
    /**
     * Actualiza una tarea existente (solo ADMIN/OPERADOR)
     */
    TareaResponse update(Long id, TareaRequest request);
    
    /**
     * Inicia una tarea (cambia estado a EN_PROCESO y registra fecha de inicio)
     * Solo el operador asignado o ADMIN puede iniciar
     */
    TareaResponse iniciar(Long id, String username);
    
    /**
     * Pausa una tarea (cambia estado a PAUSADO)
     * Solo el operador asignado o ADMIN puede pausar
     */
    TareaResponse pausar(Long id, String username, String observaciones);
    
    /**
     * Completa una tarea (cambia estado a COMPLETADO y registra fecha de fin)
     * Solo el operador asignado o ADMIN puede completar
     */
    TareaResponse completar(Long id, String username, String observaciones);
    
    /**
     * Elimina una tarea (solo ADMIN)
     */
    void delete(Long id);
    
    /**
     * Verifica si un usuario es el operador asignado de una tarea
     */
    boolean isAssignedOperador(Long tareaId, String username);
}
