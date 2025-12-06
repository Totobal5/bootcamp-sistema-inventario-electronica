package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.Tarea;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    
    /**
     * Busca todas las tareas de un operador específico
     */
    List<Tarea> findByOperadorId(Long operadorId);
    
    /**
     * Busca tareas por estado
     */
    List<Tarea> findByEstado(EstadoSolicitud estado);
    
    /**
     * Busca tareas de una solicitud de armado
     */
    List<Tarea> findBySolicitudArmadoId(Long solicitudId);
    
    /**
     * Busca tareas de un operador por estado
     */
    List<Tarea> findByOperadorIdAndEstado(Long operadorId, EstadoSolicitud estado);
    
    /**
     * Busca tarea con JOIN FETCH de operador y solicitud
     */
    @Query("SELECT t FROM Tarea t " +
           "LEFT JOIN FETCH t.operador " +
           "LEFT JOIN FETCH t.solicitudArmado sa " +
           "LEFT JOIN FETCH sa.pcbDesign " +
           "WHERE t.id = :id")
    Optional<Tarea> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Busca tareas de un operador con JOIN FETCH de solicitud
     */
    @Query("SELECT t FROM Tarea t " +
           "LEFT JOIN FETCH t.solicitudArmado sa " +
           "LEFT JOIN FETCH sa.pcbDesign " +
           "WHERE t.operador.id = :operadorId")
    List<Tarea> findByOperadorIdWithSolicitud(@Param("operadorId") Long operadorId);
    
    /**
     * Cuenta tareas por estado
     */
    long countByEstado(EstadoSolicitud estado);
    
    /**
     * Cuenta tareas de un operador por estado
     */
    long countByOperadorIdAndEstado(Long operadorId, EstadoSolicitud estado);
}
