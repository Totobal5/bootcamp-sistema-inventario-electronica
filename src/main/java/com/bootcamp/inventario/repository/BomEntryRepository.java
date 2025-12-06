package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.BomEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad BomEntry
 */
@Repository
public interface BomEntryRepository extends JpaRepository<BomEntry, Long> {

    /**
     * Busca todas las entradas BOM de un diseño PCB
     */
    List<BomEntry> findByPcbDesignId(Long pcbDesignId);

    /**
     * Busca todas las entradas BOM de una revisión PCB
     */
    List<BomEntry> findByPcbRevisionId(Long pcbRevisionId);

    /**
     * Elimina todas las entradas BOM de un diseño PCB
     */
    void deleteByPcbDesignId(Long pcbDesignId);

    /**
     * Elimina todas las entradas BOM de una revisión PCB
     */
    void deleteByPcbRevisionId(Long pcbRevisionId);
}
