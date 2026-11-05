package pe.edu.unsch.sigemu.application.usecases;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.repositories.MovimientoRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.LoteNoEncontradoException;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - RegistrarMovimientoUseCase")
class RegistrarMovimientoUseCaseTest {

    @Mock
    private LoteRepositoryPort loteRepository;

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    private RegistrarMovimientoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarMovimientoUseCase(loteRepository, movimientoRepository);
    }

    @Test
    @DisplayName("registrarIngreso incrementa el stock, actualiza estado a VIGENTE, persiste y registra en Kardex")
    void debeRegistrarIngresoExitosamente() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-01", "Paracetamol", fabricacion, caducidad, 10, 5
        );

        when(loteRepository.buscarPorNumeroLote("LOT-01")).thenReturn(lote);

        LoteMedicamento resultado = useCase.registrarIngreso("LOT-01", 15, "Reposición mensual");

        assertNotNull(resultado);
        assertEquals(25, resultado.getStock());
        assertEquals(EstadoLote.VIGENTE, resultado.getEstado());
        verify(loteRepository).guardar(lote);

        ArgumentCaptor<MovimientoInventario> captor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository).registrar(captor.capture());
        MovimientoInventario mov = captor.getValue();
        assertEquals("LOT-01", mov.getNumeroLote());
        assertEquals("INGRESO", mov.getTipoMovimiento());
        assertEquals(15, mov.getCantidad());
        assertEquals(25, mov.getStockResultante());
        assertEquals("Reposición mensual", mov.getMotivo());
    }

    @Test
    @DisplayName("registrarSalida descuenta el stock, actualiza a STOCK_BAJO y registra en Kardex")
    void debeRegistrarSalidaConStockBajo() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-02", "Ibuprofeno", fabricacion, caducidad, 20, 10
        );

        when(loteRepository.buscarPorNumeroLote("LOT-02")).thenReturn(lote);

        LoteMedicamento resultado = useCase.registrarSalida("LOT-02", 15);

        assertNotNull(resultado);
        assertEquals(5, resultado.getStock());
        assertEquals(EstadoLote.STOCK_BAJO, resultado.getEstado());
        verify(loteRepository).guardar(lote);

        ArgumentCaptor<MovimientoInventario> captor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository).registrar(captor.capture());
        assertEquals("SALIDA", captor.getValue().getTipoMovimiento());
        assertEquals(5, captor.getValue().getStockResultante());
        assertEquals("Salida de stock", captor.getValue().getMotivo());
    }

    @Test
    @DisplayName("registrarSalida descuenta el stock a cero y actualiza a AGOTADO")
    void debeRegistrarSalidaConStockAgotado() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-03", "Amoxicilina", fabricacion, caducidad, 10, 5
        );

        when(loteRepository.buscarPorNumeroLote("LOT-03")).thenReturn(lote);

        LoteMedicamento resultado = useCase.registrarSalida("LOT-03", 10);

        assertNotNull(resultado);
        assertEquals(0, resultado.getStock());
        assertEquals(EstadoLote.AGOTADO, resultado.getEstado());
        verify(loteRepository).guardar(lote);
        verify(movimientoRepository).registrar(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("registrarSalida lanza ReglaNegocioException si el stock disponible es insuficiente")
    void debeFallarSiStockEsInsuficiente() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-04", "Omeprazol", fabricacion, caducidad, 5, 2
        );

        when(loteRepository.buscarPorNumeroLote("LOT-04")).thenReturn(lote);

        assertThrows(ReglaNegocioException.class, () -> useCase.registrarSalida("LOT-04", 10));
        verify(loteRepository, never()).guardar(any());
        verify(movimientoRepository, never()).registrar(any());
    }

    @Test
    @DisplayName("registrarSalida lanza ReglaNegocioException si el lote está vencido")
    void debeFallarSiLoteEstaVencido() {
        LocalDate fabricacion = LocalDate.now().minusMonths(6);
        LocalDate caducidad = LocalDate.now().minusDays(1);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-VENCIDO", "Aspirina", fabricacion, caducidad, 20, 5
        );

        when(loteRepository.buscarPorNumeroLote("LOT-VENCIDO")).thenReturn(lote);

        assertThrows(ReglaNegocioException.class, () -> useCase.registrarSalida("LOT-VENCIDO", 2));
        verify(loteRepository, never()).guardar(any());
        verify(movimientoRepository, never()).registrar(any());
    }

    @Test
    @DisplayName("registrarMovimiento despacha correctamente INGRESO y SALIDA")
    void debeDespacharMovimientoSegunTipo() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-05", "Loratadina", fabricacion, caducidad, 30, 5
        );

        when(loteRepository.buscarPorNumeroLote("LOT-05")).thenReturn(lote);

        LoteMedicamento ingreso = useCase.registrarMovimiento("LOT-05", "ingreso", 10);
        assertEquals(40, ingreso.getStock());

        LoteMedicamento salida = useCase.registrarMovimiento("LOT-05", "SALIDA", 5);
        assertEquals(35, salida.getStock());
    }

    @Test
    @DisplayName("registrarMovimiento lanza ReglaNegocioException si tipo es nulo, vacío o inválido")
    void debeFallarSiTipoEsInvalido() {
        assertThrows(ReglaNegocioException.class, () -> useCase.registrarMovimiento("LOT-01", null, 5));
        assertThrows(ReglaNegocioException.class, () -> useCase.registrarMovimiento("LOT-01", "   ", 5));
        assertThrows(ReglaNegocioException.class, () -> useCase.registrarMovimiento("LOT-01", "TRANSFERENCIA", 5));
    }

    @Test
    @DisplayName("obtenerLote lanza LoteNoEncontradoException si el lote no existe o número es vacío")
    void debeLanzarLoteNoEncontrado() {
        when(loteRepository.buscarPorNumeroLote("NO-EXISTE")).thenReturn(null);

        assertThrows(LoteNoEncontradoException.class, () -> useCase.registrarIngreso("NO-EXISTE", 5));
        assertThrows(LoteNoEncontradoException.class, () -> useCase.registrarIngreso(null, 5));
        assertThrows(LoteNoEncontradoException.class, () -> useCase.registrarIngreso("   ", 5));
    }
}
