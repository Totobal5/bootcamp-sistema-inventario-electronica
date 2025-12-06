package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ComponenteRequest;
import com.bootcamp.inventario.dto.response.ComponenteResponse;
import com.bootcamp.inventario.exception.DuplicateResourceException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.ComponenteElectronico;
import com.bootcamp.inventario.repository.ComponenteElectronicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ComponenteServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ComponenteServiceImplTest {

    @Mock
    private ComponenteElectronicoRepository componenteRepository;

    @InjectMocks
    private ComponenteServiceImpl componenteService;

    private ComponenteElectronico componente;
    private ComponenteRequest componenteRequest;

    @BeforeEach
    void setUp() {
        componente = ComponenteElectronico.builder()
                .id(1L)
                .nombre("Resistencia 1K")
                .descripcion("Resistencia de 1K Ohm")
                .stockActual(100)
                .stockMinimo(10)
                .categoria("Resistencias")
                .precioUnitario(new BigDecimal("0.50"))
                .ubicacion("Estante A-1")
                .imagenUrl("http://example.com/resistencia.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        componenteRequest = new ComponenteRequest();
        componenteRequest.setNombre("Resistencia 1K");
        componenteRequest.setDescripcion("Resistencia de 1K Ohm");
        componenteRequest.setStockActual(100);
        componenteRequest.setStockMinimo(10);
        componenteRequest.setCategoria("Resistencias");
        componenteRequest.setPrecioUnitario(new BigDecimal("0.50"));
        componenteRequest.setUbicacion("Estante A-1");
        componenteRequest.setImagenUrl("http://example.com/resistencia.jpg");
    }

    @Nested
    @DisplayName("findAll tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all components")
        void findAll_ShouldReturnAllComponents() {
            // Given
            when(componenteRepository.findAll()).thenReturn(List.of(componente));

            // When
            List<ComponenteResponse> result = componenteService.findAll();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Resistencia 1K");
            verify(componenteRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no components exist")
        void findAll_ShouldReturnEmptyList_WhenNoComponentsExist() {
            // Given
            when(componenteRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<ComponenteResponse> result = componenteService.findAll();

            // Then
            assertThat(result).isEmpty();
            verify(componenteRepository).findAll();
        }
    }

    @Nested
    @DisplayName("findById tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return component when found")
        void findById_ShouldReturnComponent_WhenFound() {
            // Given
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));

            // When
            ComponenteResponse result = componenteService.findById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNombre()).isEqualTo("Resistencia 1K");
            verify(componenteRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void findById_ShouldThrowException_WhenNotFound() {
            // Given
            when(componenteRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> componenteService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Componente");

            verify(componenteRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("findByCategoria tests")
    class FindByCategoriaTests {

        @Test
        @DisplayName("Should return components by category")
        void findByCategoria_ShouldReturnComponents() {
            // Given
            when(componenteRepository.findByCategoria("Resistencias")).thenReturn(List.of(componente));

            // When
            List<ComponenteResponse> result = componenteService.findByCategoria("Resistencias");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategoria()).isEqualTo("Resistencias");
            verify(componenteRepository).findByCategoria("Resistencias");
        }

        @Test
        @DisplayName("Should return empty list when category not found")
        void findByCategoria_ShouldReturnEmptyList_WhenCategoryNotFound() {
            // Given
            when(componenteRepository.findByCategoria("Inexistente")).thenReturn(Collections.emptyList());

            // When
            List<ComponenteResponse> result = componenteService.findByCategoria("Inexistente");

            // Then
            assertThat(result).isEmpty();
            verify(componenteRepository).findByCategoria("Inexistente");
        }
    }

    @Nested
    @DisplayName("searchByNombre tests")
    class SearchByNombreTests {

        @Test
        @DisplayName("Should return components matching name")
        void searchByNombre_ShouldReturnMatchingComponents() {
            // Given
            when(componenteRepository.findByNombreContainingIgnoreCase("Resistencia"))
                    .thenReturn(List.of(componente));

            // When
            List<ComponenteResponse> result = componenteService.searchByNombre("Resistencia");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).contains("Resistencia");
            verify(componenteRepository).findByNombreContainingIgnoreCase("Resistencia");
        }
    }

    @Nested
    @DisplayName("create tests")
    class CreateTests {

        @Test
        @DisplayName("Should create component successfully")
        void create_ShouldCreateComponent_WhenNameIsUnique() {
            // Given
            when(componenteRepository.existsByNombre(anyString())).thenReturn(false);
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenReturn(componente);

            // When
            ComponenteResponse result = componenteService.create(componenteRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Resistencia 1K");
            verify(componenteRepository).existsByNombre("Resistencia 1K");
            verify(componenteRepository).save(any(ComponenteElectronico.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when name exists")
        void create_ShouldThrowException_WhenNameExists() {
            // Given
            when(componenteRepository.existsByNombre("Resistencia 1K")).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> componenteService.create(componenteRequest))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(componenteRepository).existsByNombre("Resistencia 1K");
            verify(componenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should set default stockMinimo when null")
        void create_ShouldSetDefaultStockMinimo_WhenNull() {
            // Given
            componenteRequest.setStockMinimo(null);
            when(componenteRepository.existsByNombre(anyString())).thenReturn(false);
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                ComponenteElectronico saved = invocation.getArgument(0);
                saved.setId(1L);
                saved.setCreatedAt(LocalDateTime.now());
                return saved;
            });

            // When
            ComponenteResponse result = componenteService.create(componenteRequest);

            // Then
            verify(componenteRepository).save(argThat(c -> c.getStockMinimo() == 0));
        }
    }

    @Nested
    @DisplayName("update tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update component successfully")
        void update_ShouldUpdateComponent_WhenValid() {
            // Given
            ComponenteRequest updateRequest = new ComponenteRequest();
            updateRequest.setNombre("Resistencia 2K");
            updateRequest.setDescripcion("Resistencia de 2K Ohm actualizada");
            updateRequest.setStockActual(200);
            updateRequest.setStockMinimo(20);
            updateRequest.setCategoria("Resistencias");
            updateRequest.setPrecioUnitario(new BigDecimal("0.75"));
            updateRequest.setUbicacion("Estante B-2");
            updateRequest.setImagenUrl("http://example.com/resistencia2k.jpg");

            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            when(componenteRepository.findByNombre("Resistencia 2K")).thenReturn(Optional.empty());
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                ComponenteElectronico saved = invocation.getArgument(0);
                saved.setUpdatedAt(LocalDateTime.now());
                return saved;
            });

            // When
            ComponenteResponse result = componenteService.update(1L, updateRequest);

            // Then
            assertThat(result.getNombre()).isEqualTo("Resistencia 2K");
            verify(componenteRepository).findById(1L);
            verify(componenteRepository).save(any(ComponenteElectronico.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when component not found")
        void update_ShouldThrowException_WhenComponentNotFound() {
            // Given
            when(componenteRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> componenteService.update(999L, componenteRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(componenteRepository).findById(999L);
            verify(componenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when name exists in another component")
        void update_ShouldThrowException_WhenNameExistsInAnotherComponent() {
            // Given
            ComponenteElectronico anotherComponent = ComponenteElectronico.builder()
                    .id(2L)
                    .nombre("Resistencia 1K")
                    .build();

            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            when(componenteRepository.findByNombre("Resistencia 1K")).thenReturn(Optional.of(anotherComponent));

            // When/Then
            assertThatThrownBy(() -> componenteService.update(1L, componenteRequest))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(componenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow update with same name for same component")
        void update_ShouldAllowSameName_ForSameComponent() {
            // Given
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            when(componenteRepository.findByNombre("Resistencia 1K")).thenReturn(Optional.of(componente));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenReturn(componente);

            // When
            ComponenteResponse result = componenteService.update(1L, componenteRequest);

            // Then
            assertThat(result).isNotNull();
            verify(componenteRepository).save(any(ComponenteElectronico.class));
        }
    }

    @Nested
    @DisplayName("delete tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete component successfully")
        void delete_ShouldDeleteComponent_WhenExists() {
            // Given
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            doNothing().when(componenteRepository).delete(any(ComponenteElectronico.class));

            // When
            componenteService.delete(1L);

            // Then
            verify(componenteRepository).findById(1L);
            verify(componenteRepository).delete(componente);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when component not found")
        void delete_ShouldThrowException_WhenComponentNotFound() {
            // Given
            when(componenteRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> componenteService.delete(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(componenteRepository).findById(999L);
            verify(componenteRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("updateStock tests")
    class UpdateStockTests {

        @Test
        @DisplayName("Should update stock successfully")
        void updateStock_ShouldUpdateStock_WhenValid() {
            // Given
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                ComponenteElectronico saved = invocation.getArgument(0);
                return saved;
            });

            // When
            ComponenteResponse result = componenteService.updateStock(1L, 150);

            // Then
            assertThat(result.getStockActual()).isEqualTo(150);
            verify(componenteRepository).save(any(ComponenteElectronico.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for negative stock")
        void updateStock_ShouldThrowException_WhenStockNegative() {
            // Given
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));

            // When/Then
            assertThatThrownBy(() -> componenteService.updateStock(1L, -10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El stock no puede ser negativo");

            verify(componenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when component not found")
        void updateStock_ShouldThrowException_WhenComponentNotFound() {
            // Given
            when(componenteRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> componenteService.updateStock(999L, 100))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(componenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow setting stock to zero")
        void updateStock_ShouldAllowZero() {
            // Given
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                ComponenteElectronico saved = invocation.getArgument(0);
                return saved;
            });

            // When
            ComponenteResponse result = componenteService.updateStock(1L, 0);

            // Then
            assertThat(result.getStockActual()).isEqualTo(0);
            verify(componenteRepository).save(any(ComponenteElectronico.class));
        }
    }
}
