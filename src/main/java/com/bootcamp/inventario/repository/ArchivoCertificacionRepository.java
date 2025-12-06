package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.ArchivoCertificacion;
import com.bootcamp.inventario.model.enums.TipoArchivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivoCertificacionRepository extends JpaRepository<ArchivoCertificacion, Long> {
    
    /**
     * Busca todos los archivos de una solicitud de armado
     */
    List<ArchivoCertificacion> findBySolicitudArmadoId(Long solicitudId);
    
    /**
     * Busca archivos por tipo
     */
    List<ArchivoCertificacion> findByTipoArchivo(TipoArchivo tipoArchivo);
    
    /**
     * Busca archivos de una solicitud por tipo
     */
    List<ArchivoCertificacion> findBySolicitudArmadoIdAndTipoArchivo(Long solicitudId, TipoArchivo tipoArchivo);
    
    /**
     * Busca archivo con JOIN FETCH de solicitud
     */
    @Query("SELECT a FROM ArchivoCertificacion a " +
           "LEFT JOIN FETCH a.solicitudArmado sa " +
           "LEFT JOIN FETCH sa.placa " +
           "WHERE a.id = :id")
    Optional<ArchivoCertificacion> findByIdWithSolicitud(@Param("id") Long id);
    
    /**
     * Cuenta archivos de una solicitud
     */
    long countBySolicitudArmadoId(Long solicitudId);
    
    /**
     * Cuenta archivos por tipo
     */
    long countByTipoArchivo(TipoArchivo tipoArchivo);
}
