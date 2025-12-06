package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ComponenteRequest;
import com.bootcamp.inventario.dto.response.ComponenteResponse;

import java.util.List;

/**
 * Interfaz del servicio de componentes electrónicos
 */
public interface IComponenteService {

    /**
     * Obtiene todos los componentes
     */
    List<ComponenteResponse> findAll();

    /**
     * Obtiene un componente por ID
     */
    ComponenteResponse findById(Long id);

    /**
     * Busca componentes por categoría
     */
    List<ComponenteResponse> findByCategoria(String categoria);

    /**
     * Busca componentes por nombre (contiene)
     */
    List<ComponenteResponse> searchByNombre(String nombre);

    /**
     * Crea un nuevo componente
     */
    ComponenteResponse create(ComponenteRequest request);

    /**
     * Actualiza un componente existente
     */
    ComponenteResponse update(Long id, ComponenteRequest request);

    /**
     * Elimina un componente
     */
    void delete(Long id);

    /**
     * Actualiza el stock de un componente
     */
    ComponenteResponse updateStock(Long id, Integer cantidad);
}
