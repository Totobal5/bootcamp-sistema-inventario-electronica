package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.Placa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Placa
 */
@Repository
public interface PlacaRepository extends JpaRepository<Placa, Long> {

    /**
     * Busca placas por nombre (exacto)
     */
    Optional<Placa> findByNombre(String nombre);

    /**
     * Busca placas cuyo nombre contenga el texto dado
     */
    List<Placa> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Verifica si existe una placa con el nombre dado
     */
    Boolean existsByNombre(String nombre);

    /**
     * Obtiene una placa con sus componentes (fetch join para evitar N+1)
     */
    @Query("SELECT p FROM Placa p LEFT JOIN FETCH p.componentes WHERE p.id = :id")
    Optional<Placa> findByIdWithComponentes(Long id);
}
