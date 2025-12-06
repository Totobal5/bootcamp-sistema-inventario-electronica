package com.bootcamp.inventario.config;

import com.bootcamp.inventario.model.Usuario;
import com.bootcamp.inventario.model.enums.Rol;
import com.bootcamp.inventario.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * Configuración para inicializar datos en la base de datos
 */
@Configuration
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear usuario administrador si no existe
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setEmail("admin@inventario.com");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setRol(Rol.ADMIN);
                admin.setActivo(true);
                
                usuarioRepository.save(admin);
                log.info("✅ Usuario administrador creado exitosamente");
                log.info("   Username: admin");
                log.info("   Password: password123");
                log.info("   Email: admin@inventario.com");
            } else {
                log.info("✓ Usuario administrador ya existe en la base de datos");
            }
        };
    }
}
