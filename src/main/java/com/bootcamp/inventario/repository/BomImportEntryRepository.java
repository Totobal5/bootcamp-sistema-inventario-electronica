package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.BomImportEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para entidad BomImportEntry
 */
@Repository
public interface BomImportEntryRepository extends JpaRepository<BomImportEntry, Long> {

    /**
     * Busca todas las entradas de BOM de una revisión específica
     */
    List<BomImportEntry> findByPcbRevisionId(Long pcbRevisionId);

    /**
     * Busca entradas de BOM no vinculadas (que necesitan revisión manual)
     */
    @Query("SELECT b FROM BomImportEntry b WHERE b.pcbRevision.id = :revisionId AND b.matched = false")
    List<BomImportEntry> findUnmatchedByRevisionId(@Param("revisionId") Long revisionId);

    /**
     * Busca entradas de BOM que necesitan revisión manual
     */
    @Query("SELECT b FROM BomImportEntry b WHERE b.pcbRevision.id = :revisionId " +
           "AND (b.matched = false OR (b.matchConfidence < 1.0 AND b.manuallyVerified = false))")
    List<BomImportEntry> findNeedingManualReviewByRevisionId(@Param("revisionId") Long revisionId);

    /**
     * Busca entradas por MPN
     */
    List<BomImportEntry> findByMpnIgnoreCase(String mpn);

    /**
     * Cuenta entradas no vinculadas de una revisión
     */
    @Query("SELECT COUNT(b) FROM BomImportEntry b WHERE b.pcbRevision.id = :revisionId AND b.matched = false")
    Long countUnmatchedByRevisionId(@Param("revisionId") Long revisionId);

    /**
     * Elimina todas las entradas de BOM de una revisión
     */
    void deleteByPcbRevisionId(Long pcbRevisionId);
}
