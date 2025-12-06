package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.PlacaRequest;
import com.bootcamp.inventario.dto.response.PlacaResponse;

import java.util.List;

/**
 * Interfaz del servicio de placas
 */
public interface IPlacaService {

    /**
     * Obtiene todas las placas
     */
    List<PlacaResponse> findAll();

    /**
     * Obtiene una placa por ID con sus componentes
     */
    PlacaResponse findById(Long id);

    /**
     * Busca placas por nombre (contiene)
     */
    List<PlacaResponse> searchByNombre(String nombre);

    /**
     * Crea una nueva placa con sus componentes
     */
    PlacaResponse create(PlacaRequest request);

    /**
     * Actualiza una placa existente
     */
    PlacaResponse update(Long id, PlacaRequest request);

    /**
     * Elimina una placa
     */
    void delete(Long id);

    /**
     * Verifica si hay stock disponible para armar una placa
     */
    Boolean verificarDisponibilidadStock(Long placaId, Integer cantidad);
}
