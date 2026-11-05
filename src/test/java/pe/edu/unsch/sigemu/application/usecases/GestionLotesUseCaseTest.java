package pe.edu.unsch.sigemu.application.usecases;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - GestionLotesUseCase")
class GestionLotesUseCaseTest {

    @Mock
    private LoteRepositoryPort loteRepository;

    private GestionLotesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GestionLotesUseCase(loteRepository);
    }

    @Test
    @DisplayName("crearLote persiste y retorna el lote cuando el número no existe previamente")
    void debeCrearLoteExitosamente() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento nuevoLote = new LoteMedicamento(
                "LOT-001",
                "Paracetamol 500mg",
                fabricacion,
                caducidad,
                50,
                10
        );

        LoteMedicamento lotePersistido = new LoteMedicamento(
                1L,
                "LOT-001",
                "Paracetamol 500mg",
                fabricacion,
                caducidad,
                50,
                10
        );

        when(loteRepository.buscarPorNumeroLote("LOT-001"))
                .thenReturn(null)
                .thenReturn(lotePersistido);

        LoteMedicamento resultado = useCase.crearLote(nuevoLote);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("LOT-001", resultado.getNumeroLote());
        verify(loteRepository).guardar(nuevoLote);
    }

    @Test
    @DisplayName("crearLote lanza ReglaNegocioException si ya existe un lote con ese número")
    void debeFallarSiLoteYaExiste() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento nuevoLote = new LoteMedicamento(
                "LOT-EXISTENTE",
                "Paracetamol 500mg",
                fabricacion,
                caducidad,
                50,
                10
        );

        when(loteRepository.buscarPorNumeroLote("LOT-EXISTENTE")).thenReturn(nuevoLote);

        assertThrows(ReglaNegocioException.class, () -> useCase.crearLote(nuevoLote));
        verify(loteRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("crearLote lanza ReglaNegocioException si el lote es nulo")
    void debeFallarSiLoteEsNulo() {
        assertThrows(ReglaNegocioException.class, () -> useCase.crearLote(null));
    }

    @Test
    @DisplayName("listarTodos delega al repositorio y retorna los lotes")
    void debeListarTodos() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                1L, "LOT-1", "Medicamento", fabricacion, caducidad, 20, 5
        );

        when(loteRepository.listarTodos()).thenReturn(List.of(lote));

        List<LoteMedicamento> resultado = useCase.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("LOT-1", resultado.get(0).getNumeroLote());
        verify(loteRepository).listarTodos();
    }

    @Test
    @DisplayName("buscarPorNumero retorna lote o null si no existe o es vacío")
    void debeBuscarPorNumero() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                1L, "LOT-1", "Medicamento", fabricacion, caducidad, 20, 5
        );

        when(loteRepository.buscarPorNumeroLote("LOT-1")).thenReturn(lote);

        LoteMedicamento resultado = useCase.buscarPorNumero("LOT-1");
        assertNotNull(resultado);
        assertEquals("LOT-1", resultado.getNumeroLote());

        assertNull(useCase.buscarPorNumero(null));
        assertNull(useCase.buscarPorNumero("   "));
    }

    @Test
    @DisplayName("buscarPorMedicamento retorna coincidencias o lista vacía si texto es nulo/vacío")
    void debeBuscarPorMedicamento() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                1L, "LOT-1", "Amoxicilina", fabricacion, caducidad, 20, 5
        );

        when(loteRepository.buscarPorMedicamento("amox")).thenReturn(List.of(lote));

        List<LoteMedicamento> resultado = useCase.buscarPorMedicamento("amox");
        assertEquals(1, resultado.size());

        assertTrue(useCase.buscarPorMedicamento(null).isEmpty());
        assertTrue(useCase.buscarPorMedicamento("   ").isEmpty());
    }
}
