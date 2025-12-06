package com.bootcamp.inventario.controller;

import com.bootcamp.inventario.dto.request.ComponenteRequest;
import com.bootcamp.inventario.dto.response.ComponenteResponse;
import com.bootcamp.inventario.exception.DuplicateResourceException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.service.IComponenteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ComponenteController
 */
@SpringBootTest
@AutoConfigureMockMvc
class ComponenteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IComponenteService componenteService;

    private ComponenteResponse componenteResponse;
    private ComponenteRequest componenteRequest;

    @BeforeEach
    void setUp() {
        componenteResponse = ComponenteResponse.builder()
                .id(1L)
                .nombre("Resistencia 1K")
                .codigoInterno("RES-1K-001")
                .descripcion("Resistencia de 1K Ohm")
                .stockActual(100)
                .stockMinimo(10)
                .categoria("Resistencias")
                .precioUnitario(new BigDecimal("0.50"))
                .ubicacion("Estante A-1")
                .urlCompra("https://www.ejemplo.com/resistencia-1k")
                .imagenUrl("http://example.com/resistencia.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        componenteRequest = new ComponenteRequest();
        componenteRequest.setNombre("Resistencia 1K");
        componenteRequest.setCodigoInterno("RES-1K-001");
        componenteRequest.setDescripcion("Resistencia de 1K Ohm");
        componenteRequest.setStockActual(100);
        componenteRequest.setStockMinimo(10);
        componenteRequest.setCategoria("Resistencias");
        componenteRequest.setPrecioUnitario(new BigDecimal("0.50"));
        componenteRequest.setUbicacion("Estante A-1");
        componenteRequest.setUrlCompra("https://www.ejemplo.com/resistencia-1k");
        componenteRequest.setImagenUrl("http://example.com/resistencia.jpg");
    }

    @Nested
    @DisplayName("GET /api/componentes tests")
    class GetAllComponentesTests {

        @Test
        @DisplayName("Should return all components without authentication")
        void getAllComponentes_ShouldReturnAllComponents() throws Exception {
            // Given
            when(componenteService.findAll()).thenReturn(List.of(componenteResponse));

            // When/Then
            mockMvc.perform(get("/api/componentes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Resistencia 1K"))
                    .andExpect(jsonPath("$[0].stockActual").value(100));

            verify(componenteService).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no components exist")
        void getAllComponentes_ShouldReturnEmptyList() throws Exception {
            // Given
            when(componenteService.findAll()).thenReturn(Collections.emptyList());

            // When/Then
            mockMvc.perform(get("/api/componentes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/componentes/{id} tests")
    class GetComponenteByIdTests {

        @Test
        @DisplayName("Should return component by ID")
        void getComponenteById_ShouldReturnComponent() throws Exception {
            // Given
            when(componenteService.findById(1L)).thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(get("/api/componentes/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Resistencia 1K"));

            verify(componenteService).findById(1L);
        }

        @Test
        @DisplayName("Should return 404 when component not found")
        void getComponenteById_ShouldReturn404_WhenNotFound() throws Exception {
            // Given
            when(componenteService.findById(999L))
                    .thenThrow(new ResourceNotFoundException("Componente", "id", 999L));

            // When/Then
            mockMvc.perform(get("/api/componentes/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/componentes/categoria/{categoria} tests")
    class GetByCategoriasTests {

        @Test
        @DisplayName("Should return components by category")
        void getByCategoria_ShouldReturnComponents() throws Exception {
            // Given
            when(componenteService.findByCategoria("Resistencias"))
                    .thenReturn(List.of(componenteResponse));

            // When/Then
            mockMvc.perform(get("/api/componentes/categoria/Resistencias"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].categoria").value("Resistencias"));

            verify(componenteService).findByCategoria("Resistencias");
        }

        @Test
        @DisplayName("Should return empty list for unknown category")
        void getByCategoria_ShouldReturnEmptyList_ForUnknownCategory() throws Exception {
            // Given
            when(componenteService.findByCategoria("Unknown"))
                    .thenReturn(Collections.emptyList());

            // When/Then
            mockMvc.perform(get("/api/componentes/categoria/Unknown"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/componentes/search tests")
    class SearchByNombreTests {

        @Test
        @DisplayName("Should return components matching name")
        void searchByNombre_ShouldReturnMatchingComponents() throws Exception {
            // Given
            when(componenteService.searchByNombre("Resistencia"))
                    .thenReturn(List.of(componenteResponse));

            // When/Then
            mockMvc.perform(get("/api/componentes/search")
                            .param("nombre", "Resistencia"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("Resistencia 1K"));

            verify(componenteService).searchByNombre("Resistencia");
        }
    }

    @Nested
    @DisplayName("POST /api/componentes tests")
    class CreateComponenteTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create component with ADMIN role")
        void createComponente_ShouldCreateComponent_WithAdminRole() throws Exception {
            // Given
            when(componenteService.create(any(ComponenteRequest.class)))
                    .thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Resistencia 1K"));

            verify(componenteService).create(any(ComponenteRequest.class));
        }

        @Test
        @WithMockUser(roles = "OPERADOR")
        @DisplayName("Should create component with OPERADOR role")
        void createComponente_ShouldCreateComponent_WithOperadorRole() throws Exception {
            // Given
            when(componenteService.create(any(ComponenteRequest.class)))
                    .thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Should return 403 for CLIENTE role")
        void createComponente_ShouldReturn403_ForClienteRole() throws Exception {
            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isForbidden());

            verify(componenteService, never()).create(any());
        }

        @Test
        @DisplayName("Should return 401 without authentication")
        void createComponente_ShouldReturn401_WithoutAuthentication() throws Exception {
            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 for invalid request - missing name")
        void createComponente_ShouldReturn400_ForMissingName() throws Exception {
            // Given
            componenteRequest.setNombre("");

            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isBadRequest());

            verify(componenteService, never()).create(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 for invalid request - negative stock")
        void createComponente_ShouldReturn400_ForNegativeStock() throws Exception {
            // Given
            componenteRequest.setStockActual(-10);

            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isBadRequest());

            verify(componenteService, never()).create(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 409 when name already exists")
        void createComponente_ShouldReturn409_WhenNameExists() throws Exception {
            // Given
            when(componenteService.create(any(ComponenteRequest.class)))
                    .thenThrow(new DuplicateResourceException("Componente", "nombre", "Resistencia 1K"));

            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 409 when codigoInterno already exists")
        void createComponente_ShouldReturn409_WhenCodigoInternoExists() throws Exception {
            // Given
            when(componenteService.create(any(ComponenteRequest.class)))
                    .thenThrow(new DuplicateResourceException("Componente", "codigoInterno", "RES-1K-001"));

            // When/Then
            mockMvc.perform(post("/api/componentes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Componente ya existe con codigoInterno: RES-1K-001"));
        }
    }

    @Nested
    @DisplayName("PUT /api/componentes/{id} tests")
    class UpdateComponenteTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update component with ADMIN role")
        void updateComponente_ShouldUpdateComponent_WithAdminRole() throws Exception {
            // Given
            when(componenteService.update(eq(1L), any(ComponenteRequest.class)))
                    .thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(put("/api/componentes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(componenteService).update(eq(1L), any(ComponenteRequest.class));
        }

        @Test
        @WithMockUser(roles = "OPERADOR")
        @DisplayName("Should update component with OPERADOR role")
        void updateComponente_ShouldUpdateComponent_WithOperadorRole() throws Exception {
            // Given
            when(componenteService.update(eq(1L), any(ComponenteRequest.class)))
                    .thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(put("/api/componentes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Should return 403 for CLIENTE role")
        void updateComponente_ShouldReturn403_ForClienteRole() throws Exception {
            // When/Then
            mockMvc.perform(put("/api/componentes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when component not found")
        void updateComponente_ShouldReturn404_WhenNotFound() throws Exception {
            // Given
            when(componenteService.update(eq(999L), any(ComponenteRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Componente", "id", 999L));

            // When/Then
            mockMvc.perform(put("/api/componentes/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componenteRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/componentes/{id}/stock tests")
    class UpdateStockTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update stock with ADMIN role")
        void updateStock_ShouldUpdateStock_WithAdminRole() throws Exception {
            // Given
            componenteResponse.setStockActual(150);
            when(componenteService.updateStock(1L, 150)).thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(patch("/api/componentes/1/stock")
                            .with(csrf())
                            .param("cantidad", "150"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.stockActual").value(150));

            verify(componenteService).updateStock(1L, 150);
        }

        @Test
        @WithMockUser(roles = "OPERADOR")
        @DisplayName("Should update stock with OPERADOR role")
        void updateStock_ShouldUpdateStock_WithOperadorRole() throws Exception {
            // Given
            when(componenteService.updateStock(1L, 150)).thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(patch("/api/componentes/1/stock")
                            .with(csrf())
                            .param("cantidad", "150"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Should return 403 for CLIENTE role")
        void updateStock_ShouldReturn403_ForClienteRole() throws Exception {
            // When/Then
            mockMvc.perform(patch("/api/componentes/1/stock")
                            .with(csrf())
                            .param("cantidad", "150"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 for negative stock (IllegalArgumentException not handled)")
        void updateStock_ShouldReturn500_ForNegativeStock() throws Exception {
            // Given - The actual service throws IllegalArgumentException which is not handled 
            // by GlobalExceptionHandler, so it returns 500
            when(componenteService.updateStock(1L, -10))
                    .thenThrow(new IllegalArgumentException("El stock no puede ser negativo"));

            // When/Then
            mockMvc.perform(patch("/api/componentes/1/stock")
                            .with(csrf())
                            .param("cantidad", "-10"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update stock to zero")
        void updateStock_ShouldUpdateStockToZero() throws Exception {
            // Given
            componenteResponse.setStockActual(0);
            when(componenteService.updateStock(1L, 0)).thenReturn(componenteResponse);

            // When/Then
            mockMvc.perform(patch("/api/componentes/1/stock")
                            .with(csrf())
                            .param("cantidad", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.stockActual").value(0));
        }
    }

    @Nested
    @DisplayName("DELETE /api/componentes/{id} tests")
    class DeleteComponenteTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete component with ADMIN role")
        void deleteComponente_ShouldDeleteComponent_WithAdminRole() throws Exception {
            // Given
            doNothing().when(componenteService).delete(1L);

            // When/Then
            mockMvc.perform(delete("/api/componentes/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Componente eliminado exitosamente"));

            verify(componenteService).delete(1L);
        }

        @Test
        @WithMockUser(roles = "OPERADOR")
        @DisplayName("Should return 403 for OPERADOR role")
        void deleteComponente_ShouldReturn403_ForOperadorRole() throws Exception {
            // When/Then
            mockMvc.perform(delete("/api/componentes/1")
                            .with(csrf()))
                    .andExpect(status().isForbidden());

            verify(componenteService, never()).delete(anyLong());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Should return 403 for CLIENTE role")
        void deleteComponente_ShouldReturn403_ForClienteRole() throws Exception {
            // When/Then
            mockMvc.perform(delete("/api/componentes/1")
                            .with(csrf()))
                    .andExpect(status().isForbidden());

            verify(componenteService, never()).delete(anyLong());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when component not found")
        void deleteComponente_ShouldReturn404_WhenNotFound() throws Exception {
            // Given
            doThrow(new ResourceNotFoundException("Componente", "id", 999L))
                    .when(componenteService).delete(999L);

            // When/Then
            mockMvc.perform(delete("/api/componentes/999")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }
}
