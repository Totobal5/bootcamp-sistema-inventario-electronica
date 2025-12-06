package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.PlacaComponenteRequest;
import com.bootcamp.inventario.dto.request.PlacaRequest;
import com.bootcamp.inventario.dto.response.PlacaComponenteResponse;
import com.bootcamp.inventario.dto.response.PlacaResponse;
import com.bootcamp.inventario.exception.DuplicateResourceException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.ComponenteElectronico;
import com.bootcamp.inventario.model.Placa;
import com.bootcamp.inventario.model.PlacaComponente;
import com.bootcamp.inventario.repository.ComponenteElectronicoRepository;
import com.bootcamp.inventario.repository.PlacaComponenteRepository;
import com.bootcamp.inventario.repository.PlacaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de placas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlacaServiceImpl implements IPlacaService {

    private final PlacaRepository placaRepository;
    private final ComponenteElectronicoRepository componenteRepository;
    private final PlacaComponenteRepository placaComponenteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlacaResponse> findAll() {
        log.debug("Obteniendo todas las placas");
        return placaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlacaResponse findById(Long id) {
        log.debug("Buscando placa con ID: {}", id);
        Placa placa = placaRepository.findByIdWithComponentes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placa", "id", id));
        return mapToResponse(placa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacaResponse> searchByNombre(String nombre) {
        log.debug("Buscando placas por nombre: {}", nombre);
        return placaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlacaResponse create(PlacaRequest request) {
        log.debug("Creando nueva placa: {}", request.getNombre());

        // Validar que el nombre no exista
        if (placaRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("Placa", "nombre", request.getNombre());
        }

        // Validar que todos los componentes existan
        validateComponentes(request.getComponentes());

        // Crear la placa
        Placa placa = Placa.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .imagenUrl(request.getImagenUrl())
                .build();

        Placa savedPlaca = placaRepository.save(placa);

        // Agregar componentes a la placa
        for (PlacaComponenteRequest compReq : request.getComponentes()) {
            ComponenteElectronico componente = componenteRepository.findById(compReq.getComponenteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", compReq.getComponenteId()));

            PlacaComponente placaComponente = PlacaComponente.builder()
                    .placa(savedPlaca)
                    .componente(componente)
                    .cantidadNecesaria(compReq.getCantidadNecesaria())
                    .build();

            placaComponenteRepository.save(placaComponente);
        }

        log.info("Placa creada exitosamente con ID: {}", savedPlaca.getId());
        return findById(savedPlaca.getId());
    }

    @Override
    @Transactional
    public PlacaResponse update(Long id, PlacaRequest request) {
        log.debug("Actualizando placa con ID: {}", id);

        Placa placa = placaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placa", "id", id));

        // Validar que el nombre no exista en otra placa
        placaRepository.findByNombre(request.getNombre())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Placa", "nombre", request.getNombre());
                    }
                });

        // Validar componentes
        validateComponentes(request.getComponentes());

        // Actualizar datos básicos
        placa.setNombre(request.getNombre());
        placa.setDescripcion(request.getDescripcion());
        placa.setImagenUrl(request.getImagenUrl());

        // Eliminar componentes anteriores
        placaComponenteRepository.deleteByPlacaId(id);

        // Agregar nuevos componentes
        for (PlacaComponenteRequest compReq : request.getComponentes()) {
            ComponenteElectronico componente = componenteRepository.findById(compReq.getComponenteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", compReq.getComponenteId()));

            PlacaComponente placaComponente = PlacaComponente.builder()
                    .placa(placa)
                    .componente(componente)
                    .cantidadNecesaria(compReq.getCantidadNecesaria())
                    .build();

            placaComponenteRepository.save(placaComponente);
        }

        placaRepository.save(placa);
        log.info("Placa actualizada exitosamente: {}", id);
        return findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando placa con ID: {}", id);

        Placa placa = placaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placa", "id", id));

        placaRepository.delete(placa);
        log.info("Placa eliminada exitosamente: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean verificarDisponibilidadStock(Long placaId, Integer cantidad) {
        log.debug("Verificando disponibilidad de stock para placa {} x{}", placaId, cantidad);

        Placa placa = placaRepository.findByIdWithComponentes(placaId)
                .orElseThrow(() -> new ResourceNotFoundException("Placa", "id", placaId));

        for (PlacaComponente pc : placa.getComponentes()) {
            int stockNecesario = pc.getCantidadNecesaria() * cantidad;
            int stockDisponible = pc.getComponente().getStock();

            if (stockDisponible < stockNecesario) {
                log.warn("Stock insuficiente para componente {}: necesario={}, disponible={}",
                        pc.getComponente().getNombre(), stockNecesario, stockDisponible);
                return false;
            }
        }

        return true;
    }

    /**
     * Valida que todos los componentes existan
     */
    private void validateComponentes(List<PlacaComponenteRequest> componentes) {
        for (PlacaComponenteRequest compReq : componentes) {
            if (!componenteRepository.existsById(compReq.getComponenteId())) {
                throw new ResourceNotFoundException("Componente", "id", compReq.getComponenteId());
            }
        }
    }

    /**
     * Mapea una entidad Placa a PlacaResponse
     */
    private PlacaResponse mapToResponse(Placa placa) {
        List<PlacaComponenteResponse> componentesResponse = placa.getComponentes().stream()
                .map(pc -> PlacaComponenteResponse.builder()
                        .componenteId(pc.getComponente().getId())
                        .componenteNombre(pc.getComponente().getNombre())
                        .componenteCategoria(pc.getComponente().getCategoria())
                        .cantidadNecesaria(pc.getCantidadNecesaria())
                        .stockDisponible(pc.getComponente().getStock())
                        .build())
                .collect(Collectors.toList());

        // Verificar si hay stock disponible
        boolean stockDisponible = componentesResponse.stream()
                .allMatch(c -> c.getStockDisponible() >= c.getCantidadNecesaria());

        return PlacaResponse.builder()
                .id(placa.getId())
                .nombre(placa.getNombre())
                .descripcion(placa.getDescripcion())
                .imagenUrl(placa.getImagenUrl())
                .componentes(componentesResponse)
                .stockDisponible(stockDisponible)
                .createdAt(placa.getCreatedAt())
                .updatedAt(placa.getUpdatedAt())
                .build();
    }
}
