package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.PlacaComponenteRequest;
import com.bootcamp.inventario.dto.request.PlacaRequest;
import com.bootcamp.inventario.dto.response.PlacaResponse;
import com.bootcamp.inventario.exception.DuplicateResourceException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.ComponenteElectronico;
import com.bootcamp.inventario.model.Placa;
import com.bootcamp.inventario.model.PlacaComponente;
import com.bootcamp.inventario.repository.ComponenteElectronicoRepository;
import com.bootcamp.inventario.repository.PlacaComponenteRepository;
import com.bootcamp.inventario.repository.PlacaRepository;
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
import java.util.ArrayList;
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
 * Unit tests for PlacaServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class PlacaServiceImplTest {

    @Mock
    private PlacaRepository placaRepository;

    @Mock
    private ComponenteElectronicoRepository componenteRepository;

    @Mock
    private PlacaComponenteRepository placaComponenteRepository;

    @InjectMocks
    private PlacaServiceImpl placaService;

    private Placa placa;
    private ComponenteElectronico componente;
    private PlacaComponente placaComponente;
    private PlacaRequest placaRequest;

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
                .build();

        placa = Placa.builder()
                .id(1L)
                .nombre("Arduino UNO Clone")
                .descripcion("Clon compatible con Arduino UNO")
                .imagenUrl("http://example.com/arduino.jpg")
                .componentes(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        placaComponente = PlacaComponente.builder()
                .id(1L)
                .placa(placa)
                .componente(componente)
                .cantidadNecesaria(5)
                .build();

        placa.getComponentes().add(placaComponente);

        PlacaComponenteRequest componenteReq = new PlacaComponenteRequest();
        componenteReq.setComponenteId(1L);
        componenteReq.setCantidadNecesaria(5);

        placaRequest = new PlacaRequest();
        placaRequest.setNombre("Arduino UNO Clone");
        placaRequest.setDescripcion("Clon compatible con Arduino UNO");
        placaRequest.setImagenUrl("http://example.com/arduino.jpg");
        placaRequest.setComponentes(List.of(componenteReq));
    }

    @Nested
    @DisplayName("findAll tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all placas")
        void findAll_ShouldReturnAllPlacas() {
            // Given
            when(placaRepository.findAll()).thenReturn(List.of(placa));

            // When
            List<PlacaResponse> result = placaService.findAll();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Arduino UNO Clone");
            verify(placaRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no placas exist")
        void findAll_ShouldReturnEmptyList_WhenNoPlacasExist() {
            // Given
            when(placaRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<PlacaResponse> result = placaService.findAll();

            // Then
            assertThat(result).isEmpty();
            verify(placaRepository).findAll();
        }
    }

    @Nested
    @DisplayName("findById tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return placa when found")
        void findById_ShouldReturnPlaca_WhenFound() {
            // Given
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When
            PlacaResponse result = placaService.findById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNombre()).isEqualTo("Arduino UNO Clone");
            assertThat(result.getComponentes()).hasSize(1);
            verify(placaRepository).findByIdWithComponentes(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void findById_ShouldThrowException_WhenNotFound() {
            // Given
            when(placaRepository.findByIdWithComponentes(anyLong())).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> placaService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Placa");

            verify(placaRepository).findByIdWithComponentes(999L);
        }
    }

    @Nested
    @DisplayName("searchByNombre tests")
    class SearchByNombreTests {

        @Test
        @DisplayName("Should return placas matching name")
        void searchByNombre_ShouldReturnMatchingPlacas() {
            // Given
            when(placaRepository.findByNombreContainingIgnoreCase("Arduino"))
                    .thenReturn(List.of(placa));

            // When
            List<PlacaResponse> result = placaService.searchByNombre("Arduino");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).contains("Arduino");
            verify(placaRepository).findByNombreContainingIgnoreCase("Arduino");
        }

        @Test
        @DisplayName("Should return empty list when no match")
        void searchByNombre_ShouldReturnEmptyList_WhenNoMatch() {
            // Given
            when(placaRepository.findByNombreContainingIgnoreCase("Inexistente"))
                    .thenReturn(Collections.emptyList());

            // When
            List<PlacaResponse> result = placaService.searchByNombre("Inexistente");

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("create tests")
    class CreateTests {

        @Test
        @DisplayName("Should create placa successfully")
        void create_ShouldCreatePlaca_WhenValid() {
            // Given
            when(placaRepository.existsByNombre(anyString())).thenReturn(false);
            when(componenteRepository.existsById(1L)).thenReturn(true);
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            when(placaRepository.save(any(Placa.class))).thenAnswer(invocation -> {
                Placa saved = invocation.getArgument(0);
                saved.setId(1L);
                saved.setCreatedAt(LocalDateTime.now());
                return saved;
            });
            when(placaComponenteRepository.save(any(PlacaComponente.class))).thenReturn(placaComponente);
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When
            PlacaResponse result = placaService.create(placaRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Arduino UNO Clone");
            verify(placaRepository).existsByNombre("Arduino UNO Clone");
            verify(placaRepository).save(any(Placa.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when name exists")
        void create_ShouldThrowException_WhenNameExists() {
            // Given
            when(placaRepository.existsByNombre("Arduino UNO Clone")).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> placaService.create(placaRequest))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(placaRepository).existsByNombre("Arduino UNO Clone");
            verify(placaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when component not found")
        void create_ShouldThrowException_WhenComponentNotFound() {
            // Given
            when(placaRepository.existsByNombre(anyString())).thenReturn(false);
            when(componenteRepository.existsById(1L)).thenReturn(false);

            // When/Then
            assertThatThrownBy(() -> placaService.create(placaRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Componente");

            verify(placaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update placa successfully")
        void update_ShouldUpdatePlaca_WhenValid() {
            // Given
            PlacaComponenteRequest componenteReq = new PlacaComponenteRequest();
            componenteReq.setComponenteId(1L);
            componenteReq.setCantidadNecesaria(10);

            PlacaRequest updateRequest = new PlacaRequest();
            updateRequest.setNombre("Arduino Mega Clone");
            updateRequest.setDescripcion("Clon compatible con Arduino Mega");
            updateRequest.setImagenUrl("http://example.com/mega.jpg");
            updateRequest.setComponentes(List.of(componenteReq));

            when(placaRepository.findById(1L)).thenReturn(Optional.of(placa));
            when(placaRepository.findByNombre("Arduino Mega Clone")).thenReturn(Optional.empty());
            when(componenteRepository.existsById(1L)).thenReturn(true);
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            doNothing().when(placaComponenteRepository).deleteByPlacaId(1L);
            when(placaComponenteRepository.save(any(PlacaComponente.class))).thenReturn(placaComponente);
            when(placaRepository.save(any(Placa.class))).thenReturn(placa);
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When
            PlacaResponse result = placaService.update(1L, updateRequest);

            // Then
            assertThat(result).isNotNull();
            verify(placaRepository).findById(1L);
            verify(placaComponenteRepository).deleteByPlacaId(1L);
            verify(placaRepository).save(any(Placa.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when placa not found")
        void update_ShouldThrowException_WhenPlacaNotFound() {
            // Given
            when(placaRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> placaService.update(999L, placaRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(placaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when name exists in another placa")
        void update_ShouldThrowException_WhenNameExistsInAnotherPlaca() {
            // Given
            Placa anotherPlaca = Placa.builder()
                    .id(2L)
                    .nombre("Arduino UNO Clone")
                    .build();

            when(placaRepository.findById(1L)).thenReturn(Optional.of(placa));
            when(placaRepository.findByNombre("Arduino UNO Clone")).thenReturn(Optional.of(anotherPlaca));

            // When/Then
            assertThatThrownBy(() -> placaService.update(1L, placaRequest))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(placaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow update with same name for same placa")
        void update_ShouldAllowSameName_ForSamePlaca() {
            // Given
            when(placaRepository.findById(1L)).thenReturn(Optional.of(placa));
            when(placaRepository.findByNombre("Arduino UNO Clone")).thenReturn(Optional.of(placa));
            when(componenteRepository.existsById(1L)).thenReturn(true);
            when(componenteRepository.findById(1L)).thenReturn(Optional.of(componente));
            doNothing().when(placaComponenteRepository).deleteByPlacaId(1L);
            when(placaComponenteRepository.save(any(PlacaComponente.class))).thenReturn(placaComponente);
            when(placaRepository.save(any(Placa.class))).thenReturn(placa);
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When
            PlacaResponse result = placaService.update(1L, placaRequest);

            // Then
            assertThat(result).isNotNull();
            verify(placaRepository).save(any(Placa.class));
        }
    }

    @Nested
    @DisplayName("delete tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete placa successfully")
        void delete_ShouldDeletePlaca_WhenExists() {
            // Given
            when(placaRepository.findById(1L)).thenReturn(Optional.of(placa));
            doNothing().when(placaRepository).delete(any(Placa.class));

            // When
            placaService.delete(1L);

            // Then
            verify(placaRepository).findById(1L);
            verify(placaRepository).delete(placa);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when placa not found")
        void delete_ShouldThrowException_WhenPlacaNotFound() {
            // Given
            when(placaRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> placaService.delete(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(placaRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("verificarDisponibilidadStock tests")
    class VerificarDisponibilidadStockTests {

        @Test
        @DisplayName("Should return true when stock is sufficient")
        void verificarDisponibilidadStock_ShouldReturnTrue_WhenStockIsSufficient() {
            // Given
            // componente has stockActual=100, placaComponente needs 5 per placa
            // For 10 placas, we need 50 components (5 * 10)
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When
            Boolean result = placaService.verificarDisponibilidadStock(1L, 10);

            // Then
            assertThat(result).isTrue();
            verify(placaRepository).findByIdWithComponentes(1L);
        }

        @Test
        @DisplayName("Should return false when stock is insufficient")
        void verificarDisponibilidadStock_ShouldReturnFalse_WhenStockIsInsufficient() {
            // Given
            // componente has stockActual=100, placaComponente needs 5 per placa
            // For 30 placas, we need 150 components (5 * 30) - not enough
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When
            Boolean result = placaService.verificarDisponibilidadStock(1L, 30);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for exact stock match")
        void verificarDisponibilidadStock_ShouldReturnTrue_WhenExactStockMatch() {
            // Given
            // componente has stockActual=100, placaComponente needs 5 per placa
            // For 20 placas, we need exactly 100 components (5 * 20)
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When
            Boolean result = placaService.verificarDisponibilidadStock(1L, 20);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when placa not found")
        void verificarDisponibilidadStock_ShouldThrowException_WhenPlacaNotFound() {
            // Given
            when(placaRepository.findByIdWithComponentes(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> placaService.verificarDisponibilidadStock(999L, 10))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should handle multiple components")
        void verificarDisponibilidadStock_ShouldHandleMultipleComponents() {
            // Given
            ComponenteElectronico componente2 = ComponenteElectronico.builder()
                    .id(2L)
                    .nombre("Capacitor 100uF")
                    .stockActual(50)
                    .build();

            PlacaComponente placaComponente2 = PlacaComponente.builder()
                    .id(2L)
                    .placa(placa)
                    .componente(componente2)
                    .cantidadNecesaria(10)
                    .build();

            placa.getComponentes().add(placaComponente2);

            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When - need 5*5=25 resistencias and 10*5=50 capacitors
            Boolean result = placaService.verificarDisponibilidadStock(1L, 5);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when one component has insufficient stock")
        void verificarDisponibilidadStock_ShouldReturnFalse_WhenOneComponentInsufficient() {
            // Given
            ComponenteElectronico componente2 = ComponenteElectronico.builder()
                    .id(2L)
                    .nombre("Capacitor 100uF")
                    .stockActual(5) // Very low stock
                    .build();

            PlacaComponente placaComponente2 = PlacaComponente.builder()
                    .id(2L)
                    .placa(placa)
                    .componente(componente2)
                    .cantidadNecesaria(10)
                    .build();

            placa.getComponentes().add(placaComponente2);

            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When - need 5*10=50 resistencias (OK) and 10*10=100 capacitors (NOT OK)
            Boolean result = placaService.verificarDisponibilidadStock(1L, 10);

            // Then
            assertThat(result).isFalse();
        }
    }
}
