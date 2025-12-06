package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.SolicitudMecanizado;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudMecanizadoRepository extends JpaRepository<SolicitudMecanizado, Long> {
    
    /**
     * Busca todas las solicitudes de un cliente específico
     */
    List<SolicitudMecanizado> findByClienteId(Long clienteId);
    
    /**
     * Busca solicitudes por estado
     */
    List<SolicitudMecanizado> findByEstado(EstadoSolicitud estado);
    
    /**
     * Busca solicitudes por nombre de placa (case-insensitive)
     */
    List<SolicitudMecanizado> findByNombrePlacaContainingIgnoreCase(String nombrePlaca);
    
    /**
     * Busca solicitudes de un cliente por estado
     */
    List<SolicitudMecanizado> findByClienteIdAndEstado(Long clienteId, EstadoSolicitud estado);
    
    /**
     * Busca solicitudes con JOIN FETCH del cliente para evitar N+1
     */
    @Query("SELECT s FROM SolicitudMecanizado s " +
           "LEFT JOIN FETCH s.cliente " +
           "WHERE s.id = :id")
    SolicitudMecanizado findByIdWithCliente(@Param("id") Long id);
    
    /**
     * Cuenta solicitudes por estado
     */
    long countByEstado(EstadoSolicitud estado);
}
