package com.bootcamp.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación (login/register)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de autenticación exitosa")
public class AuthResponse {

    @Schema(description = "Token JWT de sesión", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Builder.Default
    @Schema(description = "Tipo de token", example = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;
    
    @Schema(description = "Email del usuario", example = "admin@empresa.com")
    private String email;
    
    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String rol;

    public AuthResponse(String token, String username, String email, String rol) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.rol = rol;
    }
}
