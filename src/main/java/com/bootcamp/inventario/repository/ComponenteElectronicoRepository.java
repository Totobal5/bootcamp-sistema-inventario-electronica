package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.ComponenteElectronico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad ComponenteElectronico
 */
@Repository
public interface ComponenteElectronicoRepository extends JpaRepository<ComponenteElectronico, Long> {

    /**
     * Busca componentes por categoría
     */
    List<ComponenteElectronico> findByCategoria(String categoria);

    /**
     * Busca un componente por nombre (exacto)
     */
    Optional<ComponenteElectronico> findByNombre(String nombre);

    /**
     * Busca componentes cuyo nombre contenga el texto dado
     */
    List<ComponenteElectronico> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca componentes con stock actual mayor o igual al especificado
     */
    List<ComponenteElectronico> findByStockActualGreaterThanEqual(Integer stockActual);

    /**
     * Verifica si existe un componente con el nombre dado
     */
    Boolean existsByNombre(String nombre);

    /**
     * Busca un componente por código interno
     */
    Optional<ComponenteElectronico> findByCodigoInterno(String codigoInterno);

    /**
     * Verifica si existe un componente con el código interno dado
     */
    Boolean existsByCodigoInterno(String codigoInterno);

    /**
     * Busca un componente por MPN (exacto)
     * Crítico para matching de BOM importado
     */
    Optional<ComponenteElectronico> findByMpn(String mpn);

    /**
     * Busca componentes cuyo MPN contenga el texto dado (case-insensitive)
     * Usado para matching parcial en importación de BOM
     */
    List<ComponenteElectronico> findByMpnContainingIgnoreCase(String mpn);

    /**
     * Verifica si existe un componente con el MPN dado
     */
    Boolean existsByMpn(String mpn);
}
