package pe.edu.unsch.sigemu.application.usecases;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.repositories.MovimientoRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.LoteNoEncontradoException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ConsultarKardexUseCase")
class ConsultarKardexUseCaseTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @Mock
    private LoteRepositoryPort loteRepository;

    private ConsultarKardexUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarKardexUseCase(movimientoRepository, loteRepository);
    }

    @Test
    @DisplayName("consultarPorLote retorna la lista de movimientos cuando el lote existe")
    void debeConsultarKardexExitosamente() {
        String numeroLote = "LOT-100";
        LoteMedicamento lote = new LoteMedicamento(
                numeroLote, "Paracetamol", LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6), 50, 10
        );

        MovimientoInventario mov = new MovimientoInventario(
                1L, numeroLote, "INGRESO", 50, 50, LocalDateTime.now(), "Stock inicial"
        );

        when(loteRepository.buscarPorNumeroLote(numeroLote)).thenReturn(lote);
        when(movimientoRepository.listarPorLote(numeroLote)).thenReturn(List.of(mov));

        List<MovimientoInventario> resultado = useCase.consultarPorLote(numeroLote);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("LOT-100", resultado.get(0).getNumeroLote());
        assertEquals("INGRESO", resultado.get(0).getTipoMovimiento());

        verify(loteRepository).buscarPorNumeroLote(numeroLote);
        verify(movimientoRepository).listarPorLote(numeroLote);
    }

    @Test
    @DisplayName("consultarPorLote lanza LoteNoEncontradoException cuando el lote no existe")
    void debeFallarSiLoteNoExiste() {
        when(loteRepository.buscarPorNumeroLote("INEXISTENTE")).thenReturn(null);

        assertThrows(LoteNoEncontradoException.class, () -> useCase.consultarPorLote("INEXISTENTE"));
        verify(movimientoRepository, never()).listarPorLote(any());
    }

    @Test
    @DisplayName("consultarPorLote lanza LoteNoEncontradoException cuando el numeroLote es null o blanco")
    void debeFallarSiNumeroLoteEsInvalido() {
        assertThrows(LoteNoEncontradoException.class, () -> useCase.consultarPorLote(null));
        assertThrows(LoteNoEncontradoException.class, () -> useCase.consultarPorLote("   "));
        verify(loteRepository, never()).buscarPorNumeroLote(any());
    }
}
