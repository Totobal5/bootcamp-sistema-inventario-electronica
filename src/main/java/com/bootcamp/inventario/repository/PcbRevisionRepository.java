package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.PcbRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para entidad PcbRevision
 */
@Repository
public interface PcbRevisionRepository extends JpaRepository<PcbRevision, Long> {

    /**
     * Busca todas las revisiones de un diseño específico
     */
    List<PcbRevision> findByPcbDesignId(Long pcbDesignId);

    /**
     * Busca una revisión por código dentro de un diseño específico
     */
    Optional<PcbRevision> findByPcbDesignIdAndRevisionCode(Long pcbDesignId, String revisionCode);

    /**
     * Busca todas las revisiones de un diseño ordenadas por fecha de creación
     */
    @Query("SELECT r FROM PcbRevision r WHERE r.pcbDesign.id = :pcbDesignId ORDER BY r.createdAt DESC")
    List<PcbRevision> findByPcbDesignIdOrderByCreatedAtDesc(@Param("pcbDesignId") Long pcbDesignId);

    /**
     * Verifica si existe una revisión con el código específico en un diseño
     */
    boolean existsByPcbDesignIdAndRevisionCode(Long pcbDesignId, String revisionCode);

    /**
     * Busca todas las revisiones por estado de ciclo de vida
     */
    @Query("SELECT r FROM PcbRevision r WHERE r.lifecycleStatus = :status")
    List<PcbRevision> findByLifecycleStatus(@Param("status") String status);

    /**
     * Busca revisiones aprobadas de un diseño
     */
    @Query("SELECT r FROM PcbRevision r WHERE r.pcbDesign.id = :pcbDesignId AND r.locked = true")
    List<PcbRevision> findApprovedByPcbDesignId(@Param("pcbDesignId") Long pcbDesignId);

    /**
     * Busca la última revisión de producción de un diseño
     */
    @Query("SELECT r FROM PcbRevision r WHERE r.pcbDesign.id = :pcbDesignId " +
           "AND r.lifecycleStatus = 'PRODUCTION' ORDER BY r.createdAt DESC")
    Optional<PcbRevision> findLatestProductionRevision(@Param("pcbDesignId") Long pcbDesignId);

    /**
     * Busca una revisión con todas sus entradas de BOM importado
     */
    @Query("SELECT r FROM PcbRevision r LEFT JOIN FETCH r.bomImportEntries WHERE r.id = :id")
    Optional<PcbRevision> findByIdWithBomImportEntries(@Param("id") Long id);
}
