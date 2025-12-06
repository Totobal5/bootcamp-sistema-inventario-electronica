package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.LoginRequest;
import com.bootcamp.inventario.dto.request.RegisterRequest;
import com.bootcamp.inventario.dto.response.AuthResponse;
import com.bootcamp.inventario.dto.response.MessageResponse;
import com.bootcamp.inventario.model.Usuario;
import com.bootcamp.inventario.repository.UsuarioRepository;
import com.bootcamp.inventario.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación
 * Maneja login y registro de usuarios
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol().name()
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El username ya está en uso"));
        }

        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso"));
        }

        // Crear nuevo usuario
        Usuario usuario = Usuario.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .rol(registerRequest.getRol())
                .activo(true)
                .build();

        usuarioRepository.save(usuario);

        // Generar token
        String jwt = tokenProvider.generateTokenFromUsername(usuario.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(
                        jwt,
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getRol().name()
                ));
    }
}
