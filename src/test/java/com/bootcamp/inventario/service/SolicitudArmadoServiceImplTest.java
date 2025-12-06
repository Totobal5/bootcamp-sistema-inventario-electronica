package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.request.ActualizarEstadoRequest;
import com.bootcamp.inventario.dto.request.SolicitudArmadoRequest;
import com.bootcamp.inventario.dto.response.SolicitudArmadoResponse;
import com.bootcamp.inventario.exception.BadRequestException;
import com.bootcamp.inventario.exception.InsufficientStockException;
import com.bootcamp.inventario.exception.ResourceNotFoundException;
import com.bootcamp.inventario.model.*;
import com.bootcamp.inventario.model.enums.EstadoSolicitud;
import com.bootcamp.inventario.model.enums.Rol;
import com.bootcamp.inventario.repository.ComponenteElectronicoRepository;
import com.bootcamp.inventario.repository.PlacaRepository;
import com.bootcamp.inventario.repository.SolicitudArmadoRepository;
import com.bootcamp.inventario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
 * Unit tests for SolicitudArmadoServiceImpl
 * Special emphasis on stock deduction functionality
 */
@ExtendWith(MockitoExtension.class)
class SolicitudArmadoServiceImplTest {

    @Mock
    private SolicitudArmadoRepository solicitudRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PlacaRepository placaRepository;

    @Mock
    private ComponenteElectronicoRepository componenteRepository;

    @InjectMocks
    private SolicitudArmadoServiceImpl solicitudArmadoService;

    @Captor
    private ArgumentCaptor<ComponenteElectronico> componenteCaptor;

    private Usuario cliente;
    private Placa placa;
    private ComponenteElectronico componente1;
    private ComponenteElectronico componente2;
    private PlacaComponente placaComponente1;
    private PlacaComponente placaComponente2;
    private SolicitudArmado solicitud;
    private SolicitudArmadoRequest solicitudRequest;

    @BeforeEach
    void setUp() {
        cliente = Usuario.builder()
                .id(1L)
                .username("cliente_test")
                .email("cliente@test.com")
                .password("password123")
                .rol(Rol.CLIENTE)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        componente1 = ComponenteElectronico.builder()
                .id(1L)
                .nombre("Resistencia 1K")
                .stockActual(100)
                .stockMinimo(10)
                .categoria("Resistencias")
                .precioUnitario(new BigDecimal("0.50"))
                .build();

        componente2 = ComponenteElectronico.builder()
                .id(2L)
                .nombre("Capacitor 100uF")
                .stockActual(50)
                .stockMinimo(5)
                .categoria("Capacitores")
                .precioUnitario(new BigDecimal("1.00"))
                .build();

        placa = Placa.builder()
                .id(1L)
                .nombre("Arduino UNO Clone")
                .descripcion("Clon compatible con Arduino UNO")
                .componentes(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        placaComponente1 = PlacaComponente.builder()
                .id(1L)
                .placa(placa)
                .componente(componente1)
                .cantidadNecesaria(5)
                .build();

        placaComponente2 = PlacaComponente.builder()
                .id(2L)
                .placa(placa)
                .componente(componente2)
                .cantidadNecesaria(2)
                .build();

        placa.getComponentes().add(placaComponente1);
        placa.getComponentes().add(placaComponente2);

        solicitud = SolicitudArmado.builder()
                .id(1L)
                .cliente(cliente)
                .placa(placa)
                .cantidad(10)
                .estado(EstadoSolicitud.PENDIENTE)
                .observaciones("Solicitud de prueba")
                .certificaciones(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        solicitudRequest = SolicitudArmadoRequest.builder()
                .placaId(1L)
                .cantidad(10)
                .observaciones("Solicitud de prueba")
                .build();
    }

    @Nested
    @DisplayName("findAll tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all solicitudes")
        void findAll_ShouldReturnAllSolicitudes() {
            // Given
            when(solicitudRepository.findAll()).thenReturn(List.of(solicitud));

            // When
            List<SolicitudArmadoResponse> result = solicitudArmadoService.findAll();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPlacaNombre()).isEqualTo("Arduino UNO Clone");
            verify(solicitudRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no solicitudes exist")
        void findAll_ShouldReturnEmptyList_WhenNoSolicitudesExist() {
            // Given
            when(solicitudRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<SolicitudArmadoResponse> result = solicitudArmadoService.findAll();

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return solicitud when found")
        void findById_ShouldReturnSolicitud_WhenFound() {
            // Given
            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));

            // When
            SolicitudArmadoResponse result = solicitudArmadoService.findById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCantidad()).isEqualTo(10);
            verify(solicitudRepository).findByIdWithRelations(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void findById_ShouldThrowException_WhenNotFound() {
            // Given
            when(solicitudRepository.findByIdWithRelations(anyLong())).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Solicitud de Armado");
        }
    }

    @Nested
    @DisplayName("findByEstado tests")
    class FindByEstadoTests {

        @Test
        @DisplayName("Should return solicitudes by estado")
        void findByEstado_ShouldReturnSolicitudes() {
            // Given
            when(solicitudRepository.findByEstado(EstadoSolicitud.PENDIENTE))
                    .thenReturn(List.of(solicitud));

            // When
            List<SolicitudArmadoResponse> result = 
                    solicitudArmadoService.findByEstado(EstadoSolicitud.PENDIENTE);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        }
    }

    @Nested
    @DisplayName("findByClienteId tests")
    class FindByClienteIdTests {

        @Test
        @DisplayName("Should return solicitudes by cliente")
        void findByClienteId_ShouldReturnSolicitudes() {
            // Given
            when(solicitudRepository.findByClienteIdWithPlaca(1L)).thenReturn(List.of(solicitud));

            // When
            List<SolicitudArmadoResponse> result = solicitudArmadoService.findByClienteId(1L);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getClienteId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("create tests")
    class CreateTests {

        @Test
        @DisplayName("Should create solicitud when stock is sufficient")
        void create_ShouldCreateSolicitud_WhenStockIsSufficient() {
            // Given
            when(usuarioRepository.findByUsername("cliente_test")).thenReturn(Optional.of(cliente));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenReturn(solicitud);

            // When
            SolicitudArmadoResponse result = solicitudArmadoService.create(solicitudRequest, "cliente_test");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getCantidad()).isEqualTo(10);
            verify(usuarioRepository).findByUsername("cliente_test");
            verify(placaRepository).findByIdWithComponentes(1L);
            verify(solicitudRepository).save(any(SolicitudArmado.class));
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when stock is insufficient")
        void create_ShouldThrowException_WhenStockIsInsufficient() {
            // Given
            solicitudRequest.setCantidad(1000); // Requesting too many
            when(usuarioRepository.findByUsername("cliente_test")).thenReturn(Optional.of(cliente));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.create(solicitudRequest, "cliente_test"))
                    .isInstanceOf(InsufficientStockException.class);

            verify(solicitudRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found")
        void create_ShouldThrowException_WhenUserNotFound() {
            // Given
            when(usuarioRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.create(solicitudRequest, "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Usuario");

            verify(solicitudRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when placa not found")
        void create_ShouldThrowException_WhenPlacaNotFound() {
            // Given
            when(usuarioRepository.findByUsername("cliente_test")).thenReturn(Optional.of(cliente));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.create(solicitudRequest, "cliente_test"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Placa");

            verify(solicitudRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate stock for all components")
        void create_ShouldValidateStockForAllComponents() {
            // Given
            // For 10 placas: need 5*10=50 resistencias and 2*10=20 capacitors
            // componente1 has 100 (sufficient), componente2 has 50 (sufficient)
            when(usuarioRepository.findByUsername("cliente_test")).thenReturn(Optional.of(cliente));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenReturn(solicitud);

            // When
            SolicitudArmadoResponse result = solicitudArmadoService.create(solicitudRequest, "cliente_test");

            // Then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should fail when any single component has insufficient stock")
        void create_ShouldFail_WhenSingleComponentHasInsufficientStock() {
            // Given
            componente2.setStockActual(5); // Very low stock for capacitor
            solicitudRequest.setCantidad(10); // Need 2*10=20 capacitors

            when(usuarioRepository.findByUsername("cliente_test")).thenReturn(Optional.of(cliente));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.create(solicitudRequest, "cliente_test"))
                    .isInstanceOf(InsufficientStockException.class);

            verify(solicitudRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizarEstado tests")
    class ActualizarEstadoTests {

        @Test
        @DisplayName("Should update estado from PENDIENTE to EN_PROCESO")
        void actualizarEstado_ShouldUpdateEstado_FromPendienteToEnProceso() {
            // Given
            ActualizarEstadoRequest request = ActualizarEstadoRequest.builder()
                    .estado(EstadoSolicitud.EN_PROCESO)
                    .build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                SolicitudArmado saved = invocation.getArgument(0);
                return saved;
            });

            // When
            SolicitudArmadoResponse result = solicitudArmadoService.actualizarEstado(1L, request);

            // Then
            assertThat(result.getEstado()).isEqualTo(EstadoSolicitud.EN_PROCESO);
        }

        @Test
        @DisplayName("Should throw BadRequestException when trying to change COMPLETADO state")
        void actualizarEstado_ShouldThrowException_WhenCompletado() {
            // Given
            solicitud.setEstado(EstadoSolicitud.COMPLETADO);
            ActualizarEstadoRequest request = ActualizarEstadoRequest.builder()
                    .estado(EstadoSolicitud.CANCELADO)
                    .build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.actualizarEstado(1L, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("COMPLETADO");
        }

        @Test
        @DisplayName("Should throw BadRequestException when trying to change CANCELADO state")
        void actualizarEstado_ShouldThrowException_WhenCancelado() {
            // Given
            solicitud.setEstado(EstadoSolicitud.CANCELADO);
            ActualizarEstadoRequest request = ActualizarEstadoRequest.builder()
                    .estado(EstadoSolicitud.EN_PROCESO)
                    .build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.actualizarEstado(1L, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("CANCELADO");
        }

        @Test
        @DisplayName("Should throw BadRequestException when setting same estado")
        void actualizarEstado_ShouldThrowException_WhenSameEstado() {
            // Given
            ActualizarEstadoRequest request = ActualizarEstadoRequest.builder()
                    .estado(EstadoSolicitud.PENDIENTE)
                    .build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.actualizarEstado(1L, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("igual");
        }

        @Test
        @DisplayName("Should throw BadRequestException when trying to set COMPLETADO directly")
        void actualizarEstado_ShouldThrowException_WhenSettingCompletadoDirectly() {
            // Given
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            ActualizarEstadoRequest request = ActualizarEstadoRequest.builder()
                    .estado(EstadoSolicitud.COMPLETADO)
                    .build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.actualizarEstado(1L, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("confirmar-armado");
        }
    }

    @Nested
    @DisplayName("confirmarArmado tests - Stock Deduction")
    class ConfirmarArmadoTests {

        @Test
        @DisplayName("Should deduct stock when confirming armado")
        void confirmarArmado_ShouldDeductStock_WhenConfirmed() {
            // Given
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setCantidad(10);
            
            // Initial stock: componente1=100, componente2=50
            // Need: 5*10=50 for componente1, 2*10=20 for componente2
            // After: componente1=50, componente2=30

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

            // When
            SolicitudArmadoResponse result = solicitudArmadoService.confirmarArmado(1L);

            // Then
            assertThat(result.getEstado()).isEqualTo(EstadoSolicitud.COMPLETADO);
            
            // Verify stock was saved for both components
            verify(componenteRepository, times(2)).save(componenteCaptor.capture());
            List<ComponenteElectronico> savedComponents = componenteCaptor.getAllValues();
            
            // Check that stock was deducted correctly
            ComponenteElectronico savedResistencia = savedComponents.stream()
                    .filter(c -> c.getNombre().equals("Resistencia 1K"))
                    .findFirst().orElseThrow();
            assertThat(savedResistencia.getStockActual()).isEqualTo(50); // 100 - 50
            
            ComponenteElectronico savedCapacitor = savedComponents.stream()
                    .filter(c -> c.getNombre().equals("Capacitor 100uF"))
                    .findFirst().orElseThrow();
            assertThat(savedCapacitor.getStockActual()).isEqualTo(30); // 50 - 20
        }

        @Test
        @DisplayName("Should throw BadRequestException when not in EN_PROCESO state")
        void confirmarArmado_ShouldThrowException_WhenNotEnProceso() {
            // Given
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.confirmarArmado(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("EN_PROCESO");

            verify(componenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when stock changed after creation")
        void confirmarArmado_ShouldThrowException_WhenStockChangedAfterCreation() {
            // Given
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setCantidad(10);
            
            // Simulate stock reduced by someone else
            componente1.setStockActual(10); // Now only 10, but we need 50

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.confirmarArmado(1L))
                    .isInstanceOf(InsufficientStockException.class);

            verify(componenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should calculate correct stock deduction for multiple quantities")
        void confirmarArmado_ShouldCalculateCorrectDeduction() {
            // Given
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setCantidad(5);
            
            // For 5 placas: 5*5=25 resistencias, 2*5=10 capacitors
            // After: 100-25=75 resistencias, 50-10=40 capacitors

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

            // When
            solicitudArmadoService.confirmarArmado(1L);

            // Then
            verify(componenteRepository, times(2)).save(componenteCaptor.capture());
            List<ComponenteElectronico> savedComponents = componenteCaptor.getAllValues();
            
            ComponenteElectronico savedResistencia = savedComponents.stream()
                    .filter(c -> c.getNombre().equals("Resistencia 1K"))
                    .findFirst().orElseThrow();
            assertThat(savedResistencia.getStockActual()).isEqualTo(75);
            
            ComponenteElectronico savedCapacitor = savedComponents.stream()
                    .filter(c -> c.getNombre().equals("Capacitor 100uF"))
                    .findFirst().orElseThrow();
            assertThat(savedCapacitor.getStockActual()).isEqualTo(40);
        }

        @Test
        @DisplayName("Should deduct to zero when using all stock")
        void confirmarArmado_ShouldDeductToZero_WhenUsingAllStock() {
            // Given
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setCantidad(20);
            
            // For 20 placas: 5*20=100 resistencias (exactly all), 2*20=40 capacitors
            // After: 100-100=0 resistencias

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

            // When
            solicitudArmadoService.confirmarArmado(1L);

            // Then
            verify(componenteRepository, times(2)).save(componenteCaptor.capture());
            List<ComponenteElectronico> savedComponents = componenteCaptor.getAllValues();
            
            ComponenteElectronico savedResistencia = savedComponents.stream()
                    .filter(c -> c.getNombre().equals("Resistencia 1K"))
                    .findFirst().orElseThrow();
            assertThat(savedResistencia.getStockActual()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should change estado to COMPLETADO after stock deduction")
        void confirmarArmado_ShouldChangeEstadoToCompletado() {
            // Given
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

            // When
            SolicitudArmadoResponse result = solicitudArmadoService.confirmarArmado(1L);

            // Then
            assertThat(result.getEstado()).isEqualTo(EstadoSolicitud.COMPLETADO);
            
            // Verify the solicitud was saved with COMPLETADO estado
            ArgumentCaptor<SolicitudArmado> solicitudCaptor = ArgumentCaptor.forClass(SolicitudArmado.class);
            verify(solicitudRepository).save(solicitudCaptor.capture());
            assertThat(solicitudCaptor.getValue().getEstado()).isEqualTo(EstadoSolicitud.COMPLETADO);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when solicitud not found")
        void confirmarArmado_ShouldThrowException_WhenSolicitudNotFound() {
            // Given
            when(solicitudRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.confirmarArmado(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(componenteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete solicitud successfully")
        void delete_ShouldDeleteSolicitud_WhenExists() {
            // Given
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            doNothing().when(solicitudRepository).delete(any(SolicitudArmado.class));

            // When
            solicitudArmadoService.delete(1L);

            // Then
            verify(solicitudRepository).findById(1L);
            verify(solicitudRepository).delete(solicitud);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when solicitud not found")
        void delete_ShouldThrowException_WhenSolicitudNotFound() {
            // Given
            when(solicitudRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.delete(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(solicitudRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("isOwner tests")
    class IsOwnerTests {

        @Test
        @DisplayName("Should return true when user is owner")
        void isOwner_ShouldReturnTrue_WhenUserIsOwner() {
            // Given
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            // When
            boolean result = solicitudArmadoService.isOwner(1L, "cliente_test");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when user is not owner")
        void isOwner_ShouldReturnFalse_WhenUserIsNotOwner() {
            // Given
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            // When
            boolean result = solicitudArmadoService.isOwner(1L, "otro_usuario");

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when solicitud not found")
        void isOwner_ShouldThrowException_WhenSolicitudNotFound() {
            // Given
            when(solicitudRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> solicitudArmadoService.isOwner(999L, "cliente_test"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Stock calculation edge cases")
    class StockCalculationEdgeCasesTests {

        @Test
        @DisplayName("Should handle placa with single component")
        void confirmarArmado_ShouldHandleSingleComponent() {
            // Given
            placa.getComponentes().clear();
            placa.getComponentes().add(placaComponente1);
            
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setCantidad(5);

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

            // When
            solicitudArmadoService.confirmarArmado(1L);

            // Then
            verify(componenteRepository, times(1)).save(any(ComponenteElectronico.class));
        }

        @Test
        @DisplayName("Should handle quantity of 1")
        void confirmarArmado_ShouldHandleQuantityOfOne() {
            // Given
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setCantidad(1);
            // For 1 placa: 5*1=5 resistencias, 2*1=2 capacitors

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

            // When
            solicitudArmadoService.confirmarArmado(1L);

            // Then
            verify(componenteRepository, times(2)).save(componenteCaptor.capture());
            List<ComponenteElectronico> savedComponents = componenteCaptor.getAllValues();
            
            ComponenteElectronico savedResistencia = savedComponents.stream()
                    .filter(c -> c.getNombre().equals("Resistencia 1K"))
                    .findFirst().orElseThrow();
            assertThat(savedResistencia.getStockActual()).isEqualTo(95); // 100 - 5
        }

        @Test
        @DisplayName("Should handle component requiring only 1 unit")
        void confirmarArmado_ShouldHandleComponentRequiringOneUnit() {
            // Given
            placaComponente1.setCantidadNecesaria(1);
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setCantidad(10);
            // For 10 placas: 1*10=10 resistencias

            when(solicitudRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(solicitud));
            when(placaRepository.findByIdWithComponentes(1L)).thenReturn(Optional.of(placa));
            when(componenteRepository.save(any(ComponenteElectronico.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            when(solicitudRepository.save(any(SolicitudArmado.class))).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

            // When
            solicitudArmadoService.confirmarArmado(1L);

            // Then
            verify(componenteRepository, times(2)).save(componenteCaptor.capture());
            List<ComponenteElectronico> savedComponents = componenteCaptor.getAllValues();
            
            ComponenteElectronico savedResistencia = savedComponents.stream()
                    .filter(c -> c.getNombre().equals("Resistencia 1K"))
                    .findFirst().orElseThrow();
            assertThat(savedResistencia.getStockActual()).isEqualTo(90); // 100 - 10
        }
    }
}
