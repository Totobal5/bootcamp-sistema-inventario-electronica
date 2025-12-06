package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.BomImportRequest;
import com.bootcamp.inventario.dto.request.PcbRevisionRequest;
import com.bootcamp.inventario.dto.response.BomImportResponse;
import com.bootcamp.inventario.dto.response.PcbRevisionResponse;

import java.util.List;

/**
 * Interfaz de servicio para gestión de revisiones de PCB
 */
public interface IPcbRevisionService {

    /**
     * Crea una nueva revisión para un diseño de PCB
     */
    PcbRevisionResponse createRevision(Long pcbDesignId, PcbRevisionRequest request, String username);

    /**
     * Obtiene una revisión por ID
     */
    PcbRevisionResponse getById(Long id);

    /**
     * Obtiene todas las revisiones de un diseño
     */
    List<PcbRevisionResponse> getByPcbDesignId(Long pcbDesignId);

    /**
     * Actualiza una revisión (solo si no está bloqueada)
     */
    PcbRevisionResponse update(Long id, PcbRevisionRequest request, String username);

    /**
     * Aprueba formalmente una revisión (la bloquea para edición)
     */
    PcbRevisionResponse approve(Long id, String username);

    /**
     * Importa un BOM desde CSV y matchea con inventario
     */
    BomImportResponse importBom(Long revisionId, BomImportRequest request, String username);

    /**
     * Promueve una revisión a producción
     */
    PcbRevisionResponse promoteToProduction(Long id, String username);

    /**
     * Depreca una revisión
     */
    PcbRevisionResponse deprecate(Long id, String username);

    /**
     * Elimina una revisión (solo si no está aprobada)
     */
    void delete(Long id, String username);
}
