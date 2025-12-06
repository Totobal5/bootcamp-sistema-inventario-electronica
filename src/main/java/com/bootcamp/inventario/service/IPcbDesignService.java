package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.PcbDesignRequest;
import com.bootcamp.inventario.dto.response.PcbDesignResponse;

import java.util.List;

/**
 * Interfaz del servicio de diseños de PCB
 */
public interface IPcbDesignService {

    /**
     * Obtiene todos los diseños de PCB
     */
    List<PcbDesignResponse> findAll();

    /**
     * Obtiene un diseño de PCB por ID con sus componentes
     */
    PcbDesignResponse findById(Long id);

    /**
     * Busca diseños de PCB por nombre (contiene)
     */
    List<PcbDesignResponse> searchByNombre(String nombre);

    /**
     * Crea un nuevo diseño de PCB con sus componentes
     */
    PcbDesignResponse create(PcbDesignRequest request);

    /**
     * Actualiza un diseño de PCB existente
     */
    PcbDesignResponse update(Long id, PcbDesignRequest request);

    /**
     * Elimina un diseño de PCB
     */
    void delete(Long id);

    /**
     * Verifica si hay stock disponible para armar un diseño de PCB
     */
    Boolean verificarDisponibilidadStock(Long pcbDesignId, Integer cantidad);
}
