package com.bootcamp.inventario.dto.request;

import com.bootcamp.inventario.model.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de registro de usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de registro de usuario")
public class RegisterRequest {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "operador1")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Schema(description = "Correo electrónico del usuario", example = "operador1@empresa.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "securepass123")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Schema(description = "Rol del usuario en el sistema", example = "OPERADOR", 
            allowableValues = {"ADMIN", "OPERADOR", "CLIENTE"})
    private Rol rol;
}
