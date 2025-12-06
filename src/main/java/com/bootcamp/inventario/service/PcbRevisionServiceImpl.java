package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.BomCsvRow;
import com.bootcamp.inventario.dto.request.BomImportRequest;
import com.bootcamp.inventario.dto.request.PcbRevisionRequest;
import com.bootcamp.inventario.dto.response.BomImportEntryResponse;
import com.bootcamp.inventario.dto.response.BomImportResponse;
import com.bootcamp.inventario.dto.response.PcbRevisionResponse;
import com.bootcamp.inventario.exception.BadRequestException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.exception.UnauthorizedException;
import com.bootcamp.inventario.model.*;
import com.bootcamp.inventario.model.enums.LifecycleStatus;
import com.bootcamp.inventario.model.enums.MaskColor;
import com.bootcamp.inventario.model.enums.PcbFinish;
import com.bootcamp.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de revisiones de PCB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PcbRevisionServiceImpl implements IPcbRevisionService {

    private final PcbRevisionRepository pcbRevisionRepository;
    private final PcbDesignRepository pcbDesignRepository;
    private final BomImportEntryRepository bomImportEntryRepository;
    private final ComponenteElectronicoRepository componenteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public PcbRevisionResponse createRevision(Long pcbDesignId, PcbRevisionRequest request, String username) {
        log.debug("Creando nueva revisión {} para diseño PCB ID: {}", request.getRevisionCode(), pcbDesignId);

        PcbDesign pcbDesign = pcbDesignRepository.findById(pcbDesignId)
                .orElseThrow(() -> new ResourceNotFoundException("Diseño de PCB", "id", pcbDesignId));

        // Verificar que el código de revisión sea único para este diseño
        if (pcbRevisionRepository.existsByPcbDesignIdAndRevisionCode(pcbDesignId, request.getRevisionCode())) {
            throw new BadRequestException("Ya existe una revisión con código '" + 
                    request.getRevisionCode() + "' para este diseño");
        }

        PcbRevision revision = PcbRevision.builder()
                .pcbDesign(pcbDesign)
                .revisionCode(request.getRevisionCode())
                .width(request.getWidth() != null ? BigDecimal.valueOf(request.getWidth()) : null)
                .height(request.getHeight() != null ? BigDecimal.valueOf(request.getHeight()) : null)
                .layers(request.getLayers())
                .thickness(request.getThickness() != null ? BigDecimal.valueOf(request.getThickness()) : null)
                .finish(request.getFinish() != null ? PcbFinish.valueOf(request.getFinish()) : null)
                .maskColor(request.getMaskColor() != null ? MaskColor.valueOf(request.getMaskColor()) : null)
                .material(request.getMaterial())
                .technicalNotes(request.getTechnicalNotes())
                .lifecycleStatus(LifecycleStatus.DRAFT)
                .locked(false)
                .build();

        PcbRevision saved = pcbRevisionRepository.save(revision);
        log.info("Revisión {} creada exitosamente con ID: {}", saved.getRevisionCode(), saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PcbRevisionResponse getById(Long id) {
        PcbRevision revision = pcbRevisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión de PCB", "id", id));
        return mapToResponse(revision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PcbRevisionResponse> getByPcbDesignId(Long pcbDesignId) {
        List<PcbRevision> revisions = pcbRevisionRepository.findByPcbDesignIdOrderByCreatedAtDesc(pcbDesignId);
        return revisions.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public PcbRevisionResponse update(Long id, PcbRevisionRequest request, String username) {
        log.debug("Actualizando revisión ID: {}", id);

        PcbRevision revision = pcbRevisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión de PCB", "id", id));

        // Validar que no esté bloqueada
        if (revision.getLocked()) {
            throw new BadRequestException("Esta revisión está aprobada y bloqueada. No puede ser editada.");
        }

        // Actualizar campos técnicos
        if (request.getWidth() != null) {
            revision.setWidth(BigDecimal.valueOf(request.getWidth()));
        }
        if (request.getHeight() != null) {
            revision.setHeight(BigDecimal.valueOf(request.getHeight()));
        }
        if (request.getLayers() != null) {
            revision.setLayers(request.getLayers());
        }
        if (request.getThickness() != null) {
            revision.setThickness(BigDecimal.valueOf(request.getThickness()));
        }
        if (request.getFinish() != null) {
            revision.setFinish(PcbFinish.valueOf(request.getFinish()));
        }
        if (request.getMaskColor() != null) {
            revision.setMaskColor(MaskColor.valueOf(request.getMaskColor()));
        }
        if (request.getMaterial() != null) {
            revision.setMaterial(request.getMaterial());
        }
        if (request.getTechnicalNotes() != null) {
            revision.setTechnicalNotes(request.getTechnicalNotes());
        }

        PcbRevision updated = pcbRevisionRepository.save(revision);
        log.info("Revisión {} actualizada exitosamente", updated.getRevisionCode());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public PcbRevisionResponse approve(Long id, String username) {
        log.debug("Aprobando revisión ID: {}", id);

        PcbRevision revision = pcbRevisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión de PCB", "id", id));

        Usuario approver = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));

        revision.approve(approver);
        PcbRevision approved = pcbRevisionRepository.save(revision);

        log.info("Revisión {} aprobada por {} y bloqueada para edición", 
                 approved.getRevisionCode(), username);

        return mapToResponse(approved);
    }

    @Override
    @Transactional
    public BomImportResponse importBom(Long revisionId, BomImportRequest request, String username) {
        log.debug("Importando BOM para revisión ID: {}, {} filas", revisionId, request.getRows().size());

        PcbRevision revision = pcbRevisionRepository.findById(revisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión de PCB", "id", revisionId));

        // Validar que no esté bloqueada
        if (revision.getLocked()) {
            throw new BadRequestException("Esta revisión está aprobada y bloqueada. No se puede modificar el BOM.");
        }

        // Si se especifica sobrescribir, eliminar BOM existente
        if (Boolean.TRUE.equals(request.getOverwrite()) && !revision.getBomImportEntries().isEmpty()) {
            log.debug("Sobrescribiendo BOM existente");
            bomImportEntryRepository.deleteByPcbRevisionId(revisionId);
            revision.getBomImportEntries().clear();
        }

        // Procesar cada fila del CSV
        List<BomImportEntryResponse> entryResponses = new ArrayList<>();
        List<String> notFoundMpns = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> insufficientStockWarnings = new ArrayList<>();
        int matchedCount = 0;

        for (BomCsvRow row : request.getRows()) {
            BomImportEntry entry = processRow(row, revision, request.getMinimumConfidence());
            
            if (entry.getMatched()) {
                matchedCount++;
                
                // Verificar stock
                if (entry.getComponente() != null) {
                    int stockActual = entry.getComponente().getStockActual();
                    if (stockActual < entry.getQuantity()) {
                        insufficientStockWarnings.add(String.format("%s: necesita %d, disponible %d",
                                entry.getMpn(), entry.getQuantity(), stockActual));
                    }
                }
            } else {
                notFoundMpns.add(entry.getMpn());
            }

            entryResponses.add(mapToEntryResponse(entry));
        }

        pcbRevisionRepository.save(revision);

        log.info("BOM importado: {}/{} componentes vinculados para revisión {}", 
                 matchedCount, request.getRows().size(), revision.getRevisionCode());

        return BomImportResponse.builder()
                .totalRows(request.getRows().size())
                .matchedRows(matchedCount)
                .unmatchedRows(request.getRows().size() - matchedCount)
                .matchSuccessRate((matchedCount * 100.0) / request.getRows().size())
                .notFoundMpns(notFoundMpns)
                .warnings(warnings)
                .insufficientStockWarnings(insufficientStockWarnings)
                .entries(entryResponses)
                .success(true)
                .message(String.format("BOM importado: %d/%d componentes vinculados (%.1f%%)",
                        matchedCount, request.getRows().size(), 
                        (matchedCount * 100.0) / request.getRows().size()))
                .build();
    }

    /**
     * Procesa una fila del CSV y realiza matching con el inventario
     */
    private BomImportEntry processRow(BomCsvRow row, PcbRevision revision, Double minimumConfidence) {
        BomImportEntry entry = BomImportEntry.builder()
                .pcbRevision(revision)
                .designators(row.getDesignators())
                .mpn(row.getMpn().trim())
                .quantity(row.getQuantity())
                .description(row.getDescription())
                .value(row.getValue())
                .footprint(row.getFootprint())
                .manufacturer(row.getManufacturer())
                .matched(false)
                .manuallyVerified(false)
                .build();

        // Intentar matching por MPN exacto
        Optional<ComponenteElectronico> exactMatch = componenteRepository.findByMpn(row.getMpn().trim());
        
        if (exactMatch.isPresent()) {
            // Match exacto
            entry.linkToComponent(exactMatch.get(), 1.0, "Match exacto por MPN");
            log.debug("Match exacto: {} -> Componente ID {}", row.getMpn(), exactMatch.get().getId());
        } else {
            // Buscar matches parciales (case-insensitive)
            List<ComponenteElectronico> partialMatches = componenteRepository
                    .findByMpnContainingIgnoreCase(row.getMpn().trim());
            
            if (!partialMatches.isEmpty()) {
                // Tomar el primer match parcial con confianza < 1.0
                ComponenteElectronico bestMatch = partialMatches.get(0);
                double confidence = calculateMatchConfidence(row.getMpn(), bestMatch.getMpn());
                
                if (confidence >= minimumConfidence) {
                    entry.linkToComponent(bestMatch, confidence, 
                            String.format("Match parcial (%.0f%% confianza): %s", 
                                    confidence * 100, bestMatch.getMpn()));
                    log.debug("Match parcial: {} -> {} (confianza: {%%})", 
                             row.getMpn(), bestMatch.getMpn(), confidence * 100);
                } else {
                    entry.markAsNotFound("MPN similar encontrado pero confianza insuficiente: " + 
                                        bestMatch.getMpn());
                }
            } else {
                entry.markAsNotFound("No se encontró componente con MPN: " + row.getMpn());
                log.warn("No se encontró match para MPN: {}", row.getMpn());
            }
        }

        BomImportEntry saved = bomImportEntryRepository.save(entry);
        revision.getBomImportEntries().add(saved);
        return saved;
    }

    /**
     * Calcula nivel de confianza entre dos MPNs
     * Algoritmo simple de similitud por subcadenas
     */
    private double calculateMatchConfidence(String mpn1, String mpn2) {
        String m1 = mpn1.toLowerCase().trim();
        String m2 = mpn2.toLowerCase().trim();
        
        if (m1.equals(m2)) return 1.0;
        if (m1.contains(m2) || m2.contains(m1)) return 0.9;
        
        // Algoritmo básico de similitud por caracteres comunes
        int commonChars = 0;
        int minLength = Math.min(m1.length(), m2.length());
        
        for (int i = 0; i < minLength; i++) {
            if (m1.charAt(i) == m2.charAt(i)) {
                commonChars++;
            }
        }
        
        return (double) commonChars / Math.max(m1.length(), m2.length());
    }

    @Override
    @Transactional
    public PcbRevisionResponse promoteToProduction(Long id, String username) {
        PcbRevision revision = pcbRevisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión de PCB", "id", id));

        revision.promoteToProduction();
        PcbRevision updated = pcbRevisionRepository.save(revision);

        log.info("Revisión {} promovida a PRODUCTION", updated.getRevisionCode());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public PcbRevisionResponse deprecate(Long id, String username) {
        PcbRevision revision = pcbRevisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión de PCB", "id", id));

        revision.deprecate();
        PcbRevision updated = pcbRevisionRepository.save(revision);

        log.info("Revisión {} marcada como DEPRECATED", updated.getRevisionCode());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        PcbRevision revision = pcbRevisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión de PCB", "id", id));

        if (revision.getLocked()) {
            throw new BadRequestException("No se puede eliminar una revisión aprobada");
        }

        pcbRevisionRepository.delete(revision);
        log.info("Revisión {} eliminada por {}", revision.getRevisionCode(), username);
    }

    // ========== MAPPERS ==========

    private PcbRevisionResponse mapToResponse(PcbRevision revision) {
        long unmatchedCount = bomImportEntryRepository.countUnmatchedByRevisionId(revision.getId());

        return PcbRevisionResponse.builder()
                .id(revision.getId())
                .pcbDesignId(revision.getPcbDesign().getId())
                .pcbDesignName(revision.getPcbDesign().getNombre())
                .revisionCode(revision.getRevisionCode())
                .gerberPath(revision.getGerberPath())
                .bomPath(revision.getBomPath())
                .pickAndPlacePath(revision.getPickAndPlacePath())
                .width(revision.getWidth())
                .height(revision.getHeight())
                .layers(revision.getLayers())
                .thickness(revision.getThickness())
                .finish(revision.getFinish() != null ? revision.getFinish().name() : null)
                .maskColor(revision.getMaskColor() != null ? revision.getMaskColor().name() : null)
                .material(revision.getMaterial())
                .technicalNotes(revision.getTechnicalNotes())
                .lifecycleStatus(revision.getLifecycleStatus().name())
                .approvedAt(revision.getApprovedAt())
                .approvedBy(revision.getApprovedBy() != null ? revision.getApprovedBy().getUsername() : null)
                .locked(revision.getLocked())
                .bomEntriesCount(revision.getBomEntries().size())
                .bomImportEntriesCount(revision.getBomImportEntries().size())
                .unmatchedComponentsCount((int) unmatchedCount)
                .createdAt(revision.getCreatedAt())
                .updatedAt(revision.getUpdatedAt())
                .build();
    }

    private BomImportEntryResponse mapToEntryResponse(BomImportEntry entry) {
        Integer stockActual = null;
        Boolean stockSufficient = null;

        if (entry.getComponente() != null) {
            stockActual = entry.getComponente().getStockActual();
            stockSufficient = stockActual >= entry.getQuantity();
        }

        return BomImportEntryResponse.builder()
                .id(entry.getId())
                .designators(entry.getDesignators())
                .mpn(entry.getMpn())
                .quantity(entry.getQuantity())
                .description(entry.getDescription())
                .value(entry.getValue())
                .footprint(entry.getFootprint())
                .manufacturer(entry.getManufacturer())
                .componenteId(entry.getComponente() != null ? entry.getComponente().getId() : null)
                .componenteNombre(entry.getComponente() != null ? entry.getComponente().getNombre() : null)
                .matched(entry.getMatched())
                .matchConfidence(entry.getMatchConfidence())
                .matchNotes(entry.getMatchNotes())
                .manuallyVerified(entry.getManuallyVerified())
                .needsManualReview(entry.needsManualReview())
                .stockActual(stockActual)
                .stockSufficient(stockSufficient)
                .build();
    }
}
