package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.PlacaComponente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad PlacaComponente
 */
@Repository
public interface PlacaComponenteRepository extends JpaRepository<PlacaComponente, Long> {

    /**
     * Busca todos los componentes de una placa
     */
    List<PlacaComponente> findByPlacaId(Long placaId);

    /**
     * Elimina todos los componentes de una placa
     */
    void deleteByPlacaId(Long placaId);
}
