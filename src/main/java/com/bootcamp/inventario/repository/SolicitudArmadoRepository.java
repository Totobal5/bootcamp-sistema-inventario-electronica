package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.SolicitudArmado;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudArmadoRepository extends JpaRepository<SolicitudArmado, Long> {
    
    /**
     * Busca todas las solicitudes de un cliente específico
     */
    List<SolicitudArmado> findByClienteId(Long clienteId);
    
    /**
     * Busca solicitudes por estado
     */
    List<SolicitudArmado> findByEstado(EstadoSolicitud estado);
    
    /**
     * Busca solicitudes de un cliente por estado
     */
    List<SolicitudArmado> findByClienteIdAndEstado(Long clienteId, EstadoSolicitud estado);
    
    /**
     * Busca solicitudes por diseño PCB
     */
    List<SolicitudArmado> findByPcbDesignId(Long pcbDesignId);
    
    /**
     * Busca solicitudes con JOIN FETCH de cliente y diseño PCB
     */
    @Query("SELECT s FROM SolicitudArmado s " +
           "LEFT JOIN FETCH s.cliente " +
           "LEFT JOIN FETCH s.pcbDesign " +
           "WHERE s.id = :id")
    Optional<SolicitudArmado> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Busca solicitudes de un cliente con JOIN FETCH de diseño PCB
     */
    @Query("SELECT s FROM SolicitudArmado s " +
           "LEFT JOIN FETCH s.pcbDesign " +
           "WHERE s.cliente.id = :clienteId")
    List<SolicitudArmado> findByClienteIdWithPlaca(@Param("clienteId") Long clienteId);
    
    /**
     * Cuenta solicitudes por estado
     */
    long countByEstado(EstadoSolicitud estado);
}
