package pe.edu.unsch.sigemu.application.usecases;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ActualizarEstadoCaducidadUseCase")
class ActualizarEstadoCaducidadUseCaseTest {

    @Mock
    private LoteRepositoryPort loteRepository;

    private ActualizarEstadoCaducidadUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarEstadoCaducidadUseCase(loteRepository);
    }

    @Test
    @DisplayName("ejecutarRevision actualiza lotes vigentes o stock bajo con caducidad <= hoy a VENCIDO y persiste")
    void debeActualizarLotesVencidosYPersistir() {
        LocalDate hoy = LocalDate.now();

        // Lote 1: VIGENTE en BD pero con caducidad ayer (debe vencer)
        LoteMedicamento loteVigenteVencido = new LoteMedicamento(
                1L, "LOT-01", "Paracetamol", hoy.minusMonths(2), hoy.minusDays(1), 50, 10, EstadoLote.VIGENTE
        );

        // Lote 2: STOCK_BAJO en BD con caducidad hoy (debe vencer)
        LoteMedicamento loteStockBajoVencido = new LoteMedicamento(
                2L, "LOT-02", "Ibuprofeno", hoy.minusMonths(2), hoy, 5, 10, EstadoLote.STOCK_BAJO
        );

        // Lote 3: VIGENTE con caducidad futura (no debe cambiar)
        LoteMedicamento loteVigenteFuturo = new LoteMedicamento(
                3L, "LOT-03", "Amoxicilina", hoy.minusMonths(1), hoy.plusMonths(6), 100, 20, EstadoLote.VIGENTE
        );

        // Lote 4: Ya VENCIDO previamente en BD (no debe re-procesarse)
        LoteMedicamento loteYaVencido = new LoteMedicamento(
                4L, "LOT-04", "Omeprazol", hoy.minusMonths(3), hoy.minusDays(2), 20, 5, EstadoLote.VENCIDO
        );

        // Lote 5: AGOTADO (stock 0) con caducidad ayer (no es candidato vigente ni stock bajo)
        LoteMedicamento loteAgotado = new LoteMedicamento(
                5L, "LOT-05", "Loratadina", hoy.minusMonths(2), hoy.plusMonths(1), 0, 5, EstadoLote.AGOTADO
        );

        when(loteRepository.listarTodos()).thenReturn(List.of(
                loteVigenteVencido,
                loteStockBajoVencido,
                loteVigenteFuturo,
                loteYaVencido,
                loteAgotado
        ));

        int actualizados = useCase.ejecutarRevision();

        assertEquals(2, actualizados);
        assertEquals(EstadoLote.VENCIDO, loteVigenteVencido.getEstado());
        assertEquals(EstadoLote.VENCIDO, loteStockBajoVencido.getEstado());
        assertEquals(EstadoLote.VIGENTE, loteVigenteFuturo.getEstado());

        verify(loteRepository).guardar(loteVigenteVencido);
        verify(loteRepository).guardar(loteStockBajoVencido);
        verify(loteRepository, never()).guardar(loteVigenteFuturo);
        verify(loteRepository, never()).guardar(loteYaVencido);
        verify(loteRepository, never()).guardar(loteAgotado);
    }

    @Test
    @DisplayName("ejecutarRevision retorna 0 y no guarda nada cuando no hay lotes vencidos")
    void debeRetornarCeroSiNoHayVencidos() {
        LocalDate hoy = LocalDate.now();

        LoteMedicamento lote1 = new LoteMedicamento(
                1L, "LOT-01", "Paracetamol", hoy.minusMonths(1), hoy.plusMonths(6), 50, 10, EstadoLote.VIGENTE
        );
        LoteMedicamento lote2 = new LoteMedicamento(
                2L, "LOT-02", "Ibuprofeno", hoy.minusMonths(1), hoy.plusMonths(12), 20, 5, EstadoLote.VIGENTE
        );

        when(loteRepository.listarTodos()).thenReturn(List.of(lote1, lote2));

        int actualizados = useCase.ejecutarRevision();

        assertEquals(0, actualizados);
        verify(loteRepository, never()).guardar(any());
    }
}
