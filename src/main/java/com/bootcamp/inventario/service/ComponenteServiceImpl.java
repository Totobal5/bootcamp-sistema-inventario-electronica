package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ComponenteRequest;
import com.bootcamp.inventario.dto.response.ComponenteResponse;
import com.bootcamp.inventario.exception.DuplicateResourceException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.ComponenteElectronico;
import com.bootcamp.inventario.repository.ComponenteElectronicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de componentes electrónicos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComponenteServiceImpl implements IComponenteService {

    private final ComponenteElectronicoRepository componenteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ComponenteResponse> findAll() {
        log.debug("Obteniendo todos los componentes");
        return componenteRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComponenteResponse findById(Long id) {
        log.debug("Buscando componente con ID: {}", id);
        ComponenteElectronico componente = componenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", id));
        return mapToResponse(componente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComponenteResponse> findByCategoria(String categoria) {
        log.debug("Buscando componentes por categoría: {}", categoria);
        return componenteRepository.findByCategoria(categoria).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComponenteResponse> searchByNombre(String nombre) {
        log.debug("Buscando componentes por nombre: {}", nombre);
        return componenteRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComponenteResponse create(ComponenteRequest request) {
        log.debug("Creando nuevo componente: {}", request.getNombre());
        
        // Validar que el nombre no exista
        if (componenteRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("Componente", "nombre", request.getNombre());
        }

        ComponenteElectronico componente = ComponenteElectronico.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .stock(request.getStock())
                .categoria(request.getCategoria())
                .precio(request.getPrecio())
                .imagenUrl(request.getImagenUrl())
                .build();

        ComponenteElectronico saved = componenteRepository.save(componente);
        log.info("Componente creado exitosamente con ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ComponenteResponse update(Long id, ComponenteRequest request) {
        log.debug("Actualizando componente con ID: {}", id);
        
        ComponenteElectronico componente = componenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", id));

        // Validar que el nombre no exista en otro componente
        componenteRepository.findByNombre(request.getNombre())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Componente", "nombre", request.getNombre());
                    }
                });

        componente.setNombre(request.getNombre());
        componente.setDescripcion(request.getDescripcion());
        componente.setStock(request.getStock());
        componente.setCategoria(request.getCategoria());
        componente.setPrecio(request.getPrecio());
        componente.setImagenUrl(request.getImagenUrl());

        ComponenteElectronico updated = componenteRepository.save(componente);
        log.info("Componente actualizado exitosamente: {}", id);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando componente con ID: {}", id);
        
        ComponenteElectronico componente = componenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", id));

        componenteRepository.delete(componente);
        log.info("Componente eliminado exitosamente: {}", id);
    }

    @Override
    @Transactional
    public ComponenteResponse updateStock(Long id, Integer cantidad) {
        log.debug("Actualizando stock del componente {} a {}", id, cantidad);
        
        ComponenteElectronico componente = componenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", id));

        if (cantidad < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        componente.setStock(cantidad);
        ComponenteElectronico updated = componenteRepository.save(componente);
        log.info("Stock actualizado para componente {}: {}", id, cantidad);
        return mapToResponse(updated);
    }

    /**
     * Mapea una entidad ComponenteElectronico a ComponenteResponse
     */
    private ComponenteResponse mapToResponse(ComponenteElectronico componente) {
        return ComponenteResponse.builder()
                .id(componente.getId())
                .nombre(componente.getNombre())
                .descripcion(componente.getDescripcion())
                .stock(componente.getStock())
                .categoria(componente.getCategoria())
                .precio(componente.getPrecio())
                .imagenUrl(componente.getImagenUrl())
                .createdAt(componente.getCreatedAt())
                .updatedAt(componente.getUpdatedAt())
                .build();
    }
}
