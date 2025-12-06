package com.bootcamp.inventario.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Sistema de Inventario Electrónica API",
        version = "1.0.0",
        description = """
            API REST para gestión de inventario de componentes electrónicos, placas PCB, 
            solicitudes de mecanizado y armado, gestión de tareas y certificaciones.
            
            **Características principales:**
            - Gestión de componentes electrónicos con control de stock
            - Diseño de placas con relación ManyToMany a componentes
            - Solicitudes de mecanizado con upload de archivos Gerber
            - Solicitudes de armado con validación automática de stock
            - Sistema de tareas para operadores con estados (PENDIENTE → EN_PROCESO → COMPLETADO)
            - Upload de certificaciones e inspecciones (PDF, imágenes)
            - Autenticación JWT con roles: ADMIN, OPERADOR, CLIENTE
            - Manejo global de excepciones con mensajes claros
            
            **Flujo de trabajo típico:**
            1. ADMIN/OPERADOR crea componentes en inventario
            2. ADMIN/OPERADOR diseña placas con componentes necesarios
            3. CLIENTE solicita mecanizado de PCB (sube archivos Gerber)
            4. CLIENTE solicita armado de placas (valida stock disponible)
            5. OPERADOR crea tarea y la completa
            6. OPERADOR confirma armado (descuenta stock automáticamente)
            7. OPERADOR sube certificaciones de calidad
            
            **Autenticación:**
            Utilice el endpoint `/api/auth/login` para obtener un token JWT.
            Luego incluya el token en el header: `Authorization: Bearer {token}`
            """,
        contact = @Contact(
            name = "Bootcamp Desarrollo Backend",
            email = "contacto@bootcamp.com",
            url = "https://github.com/tu-usuario/bootcamp-sistema-inventario-electronica"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Servidor Local",
            url = "http://localhost:8081"
        ),
        @Server(
            description = "Servidor Producción",
            url = "https://tu-app.railway.app"
        )
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    description = "JWT token obtenido mediante /api/auth/login. Formato: Bearer {token}",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // Configuración declarativa mediante anotaciones
    // No requiere métodos adicionales
}
