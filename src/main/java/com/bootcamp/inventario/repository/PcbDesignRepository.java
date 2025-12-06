package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.PcbDesign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad PcbDesign
 */
@Repository
public interface PcbDesignRepository extends JpaRepository<PcbDesign, Long> {

    /**
     * Busca diseños PCB por nombre (exacto)
     */
    Optional<PcbDesign> findByNombre(String nombre);

    /**
     * Busca diseños PCB cuyo nombre contenga el texto dado
     */
    List<PcbDesign> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Verifica si existe un diseño PCB con el nombre dado
     */
    Boolean existsByNombre(String nombre);

    /**
     * Obtiene un diseño PCB con sus componentes (fetch join para evitar N+1)
     */
    @Query("SELECT p FROM PcbDesign p LEFT JOIN FETCH p.componentes WHERE p.id = :id")
    Optional<PcbDesign> findByIdWithComponentes(Long id);
}
