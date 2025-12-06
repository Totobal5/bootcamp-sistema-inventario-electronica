package com.bootcamp.inventario.repository;

import com.bootcamp.inventario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su username
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por su email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el username dado
     */
    Boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado
     */
    Boolean existsByEmail(String email);
}
