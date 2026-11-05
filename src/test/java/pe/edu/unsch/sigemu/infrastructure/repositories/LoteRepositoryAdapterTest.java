package pe.edu.unsch.sigemu.infrastructure.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.infrastructure.database.SpringDataLoteRepository;
import pe.edu.unsch.sigemu.infrastructure.database.entities.LoteEntity;
import pe.edu.unsch.sigemu.infrastructure.database.mappers.LoteMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - LoteRepositoryAdapter")
class LoteRepositoryAdapterTest {

    @Mock
    private SpringDataLoteRepository springDataLoteRepository;

    private LoteMapper loteMapper;
    private LoteRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        loteMapper = new LoteMapper();
        repositoryAdapter = new LoteRepositoryAdapter(springDataLoteRepository, loteMapper);
    }

    @Test
    @DisplayName("buscarPorNumeroLote retorna entidad de dominio cuando el lote existe")
    void debeRetornarLoteCuandoExiste() {
        String numeroLote = "LOT-100";
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteEntity entity = new LoteEntity(
                1L,
                numeroLote,
                "Ibuprofeno 400mg",
                fabricacion,
                caducidad,
                30,
                5,
                EstadoLote.VIGENTE
        );

        when(springDataLoteRepository.findByNumeroLote(numeroLote)).thenReturn(Optional.of(entity));

        LoteMedicamento resultado = repositoryAdapter.buscarPorNumeroLote(numeroLote);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(numeroLote, resultado.getNumeroLote());
        assertEquals("Ibuprofeno 400mg", resultado.getMedicamento());
        verify(springDataLoteRepository).findByNumeroLote(numeroLote);
    }

    @Test
    @DisplayName("buscarPorNumeroLote retorna null cuando no existe o el número es nulo/vacío")
    void debeRetornarNullCuandoNoExiste() {
        when(springDataLoteRepository.findByNumeroLote("INEXISTENTE")).thenReturn(Optional.empty());

        assertNull(repositoryAdapter.buscarPorNumeroLote("INEXISTENTE"));
        assertNull(repositoryAdapter.buscarPorNumeroLote(null));
        assertNull(repositoryAdapter.buscarPorNumeroLote("   "));
    }

    @Test
    @DisplayName("guardar inserta nueva entidad cuando el lote no existía previamente")
    void debeGuardarNuevoLote() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento domain = new LoteMedicamento(
                "LOT-NEW",
                "Amoxicilina",
                fabricacion,
                caducidad,
                40,
                10
        );

        when(springDataLoteRepository.findByNumeroLote("LOT-NEW")).thenReturn(Optional.empty());

        repositoryAdapter.guardar(domain);

        ArgumentCaptor<LoteEntity> captor = ArgumentCaptor.forClass(LoteEntity.class);
        verify(springDataLoteRepository).save(captor.capture());

        LoteEntity guardada = captor.getValue();
        assertEquals("LOT-NEW", guardada.getNumeroLote());
        assertEquals("Amoxicilina", guardada.getMedicamento());
        assertEquals(40, guardada.getStock());
        assertEquals(10, guardada.getStockMinimo());
        assertEquals(EstadoLote.VIGENTE, guardada.getEstado());
    }

    @Test
    @DisplayName("guardar actualiza la entidad existente preservando su identificador")
    void debeActualizarLoteExistente() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento domain = new LoteMedicamento(
                "LOT-UPDATE",
                "Amoxicilina Modificada",
                fabricacion,
                caducidad,
                80,
                15
        );

        LoteEntity existente = new LoteEntity(
                99L,
                "LOT-UPDATE",
                "Amoxicilina Antigua",
                fabricacion,
                caducidad,
                20,
                15,
                EstadoLote.STOCK_BAJO
        );

        when(springDataLoteRepository.findByNumeroLote("LOT-UPDATE")).thenReturn(Optional.of(existente));

        repositoryAdapter.guardar(domain);

        ArgumentCaptor<LoteEntity> captor = ArgumentCaptor.forClass(LoteEntity.class);
        verify(springDataLoteRepository).save(captor.capture());

        LoteEntity guardada = captor.getValue();
        assertEquals(99L, guardada.getId());
        assertEquals("Amoxicilina Modificada", guardada.getMedicamento());
        assertEquals(80, guardada.getStock());
        assertEquals(EstadoLote.VIGENTE, guardada.getEstado());
    }

    @Test
    @DisplayName("guardar lanza IllegalArgumentException si el lote es nulo")
    void debeLanzarExcepcionSiLoteEsNulo() {
        assertThrows(IllegalArgumentException.class, () -> repositoryAdapter.guardar(null));
    }

    @Test
    @DisplayName("listarTodos retorna lista completa de lotes mapeados a dominio")
    void debeListarTodosLosLotes() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteEntity entity1 = new LoteEntity(1L, "LOT-1", "Medicamento 1", fabricacion, caducidad, 10, 2, EstadoLote.VIGENTE);
        LoteEntity entity2 = new LoteEntity(2L, "LOT-2", "Medicamento 2", fabricacion, caducidad, 20, 5, EstadoLote.VIGENTE);

        when(springDataLoteRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<LoteMedicamento> resultado = repositoryAdapter.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("LOT-1", resultado.get(0).getNumeroLote());
        assertEquals("LOT-2", resultado.get(1).getNumeroLote());
        verify(springDataLoteRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorMedicamento retorna coincidencias o lista vacía si medicamento es nulo/vacío")
    void debeBuscarPorMedicamento() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteEntity entity = new LoteEntity(1L, "LOT-PAR", "Paracetamol Jarabe", fabricacion, caducidad, 15, 3, EstadoLote.VIGENTE);

        when(springDataLoteRepository.findByMedicamentoContainingIgnoreCase("paracet")).thenReturn(List.of(entity));

        List<LoteMedicamento> resultado = repositoryAdapter.buscarPorMedicamento("paracet");
        assertEquals(1, resultado.size());
        assertEquals("LOT-PAR", resultado.get(0).getNumeroLote());

        assertTrue(repositoryAdapter.buscarPorMedicamento(null).isEmpty());
        assertTrue(repositoryAdapter.buscarPorMedicamento("   ").isEmpty());
    }
}
