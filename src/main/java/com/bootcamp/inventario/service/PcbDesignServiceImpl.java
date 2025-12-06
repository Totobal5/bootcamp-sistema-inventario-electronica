package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.BomEntryRequest;
import com.bootcamp.inventario.dto.request.PcbDesignRequest;
import com.bootcamp.inventario.dto.response.BomEntryResponse;
import com.bootcamp.inventario.dto.response.PcbDesignResponse;
import com.bootcamp.inventario.exception.DuplicateResourceException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.BomEntry;
import com.bootcamp.inventario.model.ComponenteElectronico;
import com.bootcamp.inventario.model.PcbDesign;
import com.bootcamp.inventario.repository.BomEntryRepository;
import com.bootcamp.inventario.repository.ComponenteElectronicoRepository;
import com.bootcamp.inventario.repository.PcbDesignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de diseños de PCB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PcbDesignServiceImpl implements IPcbDesignService {

    private final PcbDesignRepository pcbDesignRepository;
    private final ComponenteElectronicoRepository componenteRepository;
    private final BomEntryRepository bomEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PcbDesignResponse> findAll() {
        log.debug("Obteniendo todos los diseños de PCB");
        return pcbDesignRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PcbDesignResponse findById(Long id) {
        log.debug("Buscando diseño de PCB con ID: {}", id);
        PcbDesign pcbDesign = pcbDesignRepository.findByIdWithComponentes(id)
                .orElseThrow(() -> new ResourceNotFoundException("PcbDesign", "id", id));
        return mapToResponse(pcbDesign);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PcbDesignResponse> searchByNombre(String nombre) {
        log.debug("Buscando diseños de PCB por nombre: {}", nombre);
        return pcbDesignRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PcbDesignResponse create(PcbDesignRequest request) {
        log.debug("Creando nuevo diseño de PCB: {}", request.getNombre());

        // Validar que el nombre no exista
        if (pcbDesignRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("PcbDesign", "nombre", request.getNombre());
        }

        // Validar que todos los componentes existan
        validateComponentes(request.getComponentes());

        // Crear el diseño de PCB
        PcbDesign pcbDesign = PcbDesign.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .imagenUrl(request.getImagenUrl())
                .build();

        PcbDesign savedPcbDesign = pcbDesignRepository.save(pcbDesign);

        // Agregar componentes al diseño (BOM)
        for (BomEntryRequest bomReq : request.getComponentes()) {
            ComponenteElectronico componente = componenteRepository.findById(bomReq.getComponenteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", bomReq.getComponenteId()));

            BomEntry bomEntry = BomEntry.builder()
                    .pcbDesign(savedPcbDesign)
                    .componente(componente)
                    .quantity(bomReq.getCantidadNecesaria())
                    .designators(bomReq.getDesignators())
                    .build();

            bomEntryRepository.save(bomEntry);
        }

        log.info("Diseño de PCB creado exitosamente con ID: {}", savedPcbDesign.getId());
        return findById(savedPcbDesign.getId());
    }

    @Override
    @Transactional
    public PcbDesignResponse update(Long id, PcbDesignRequest request) {
        log.debug("Actualizando diseño de PCB con ID: {}", id);

        PcbDesign pcbDesign = pcbDesignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PcbDesign", "id", id));

        // Validar que el nombre no exista en otro diseño
        pcbDesignRepository.findByNombre(request.getNombre())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("PcbDesign", "nombre", request.getNombre());
                    }
                });

        // Validar componentes
        validateComponentes(request.getComponentes());

        // Actualizar datos básicos
        pcbDesign.setNombre(request.getNombre());
        pcbDesign.setDescripcion(request.getDescripcion());
        pcbDesign.setImagenUrl(request.getImagenUrl());

        // Eliminar componentes anteriores
        bomEntryRepository.deleteByPcbDesignId(id);

        // Agregar nuevos componentes
        for (BomEntryRequest bomReq : request.getComponentes()) {
            ComponenteElectronico componente = componenteRepository.findById(bomReq.getComponenteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Componente", "id", bomReq.getComponenteId()));

            BomEntry bomEntry = BomEntry.builder()
                    .pcbDesign(pcbDesign)
                    .componente(componente)
                    .quantity(bomReq.getCantidadNecesaria())
                    .designators(bomReq.getDesignators())
                    .build();

            bomEntryRepository.save(bomEntry);
        }

        pcbDesignRepository.save(pcbDesign);
        log.info("Diseño de PCB actualizado exitosamente: {}", id);
        return findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando diseño de PCB con ID: {}", id);

        PcbDesign pcbDesign = pcbDesignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PcbDesign", "id", id));

        pcbDesignRepository.delete(pcbDesign);
        log.info("Diseño de PCB eliminado exitosamente: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean verificarDisponibilidadStock(Long pcbDesignId, Integer cantidad) {
        log.debug("Verificando disponibilidad de stock para diseño PCB {} x{}", pcbDesignId, cantidad);

        PcbDesign pcbDesign = pcbDesignRepository.findByIdWithComponentes(pcbDesignId)
                .orElseThrow(() -> new ResourceNotFoundException("PcbDesign", "id", pcbDesignId));

        for (BomEntry be : pcbDesign.getComponentes()) {
            int stockNecesario = be.getQuantity() * cantidad;
            int stockDisponible = be.getComponente().getStockActual();

            if (stockDisponible < stockNecesario) {
                log.warn("Stock insuficiente para componente {}: necesario={}, disponible={}",
                        be.getComponente().getNombre(), stockNecesario, stockDisponible);
                return false;
            }
        }

        return true;
    }

    /**
     * Valida que todos los componentes existan
     */
    private void validateComponentes(List<BomEntryRequest> componentes) {
        for (BomEntryRequest bomReq : componentes) {
            if (!componenteRepository.existsById(bomReq.getComponenteId())) {
                throw new ResourceNotFoundException("Componente", "id", bomReq.getComponenteId());
            }
        }
    }

    /**
     * Mapea una entidad PcbDesign a PcbDesignResponse
     */
    private PcbDesignResponse mapToResponse(PcbDesign pcbDesign) {
        List<BomEntryResponse> componentesResponse = pcbDesign.getComponentes().stream()
                .map(be -> BomEntryResponse.builder()
                        .componenteId(be.getComponente().getId())
                        .componenteNombre(be.getComponente().getNombre())
                        .componenteCategoria(be.getComponente().getCategoria())
                        .cantidadNecesaria(be.getQuantity())
                        .designators(be.getDesignators())
                        .stockDisponible(be.getComponente().getStockActual())
                        .build())
                .collect(Collectors.toList());

        // Verificar si hay stock disponible
        boolean stockDisponible = componentesResponse.stream()
                .allMatch(c -> c.getStockDisponible() >= c.getCantidadNecesaria());

        return PcbDesignResponse.builder()
                .id(pcbDesign.getId())
                .nombre(pcbDesign.getNombre())
                .descripcion(pcbDesign.getDescripcion())
                .imagenUrl(pcbDesign.getImagenUrl())
                .componentes(componentesResponse)
                .stockDisponible(stockDisponible)
                .createdAt(pcbDesign.getCreatedAt())
                .updatedAt(pcbDesign.getUpdatedAt())
                .build();
    }
}
