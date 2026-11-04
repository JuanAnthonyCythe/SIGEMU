package pe.edu.unsch.sigemu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import pe.edu.unsch.sigemu.application.usecases.RegistrarMovimientoUseCase;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

class LoteMedicamentoTest {

    @Test
    void creaLoteValidoEnEstadoVigente() {
        LoteMedicamento lote = loteConVencimientoFuturo(20, 5);

        assertEquals(EstadoLote.VIGENTE, lote.getEstado());
        assertEquals(20, lote.getStock());
    }

    @Test
    void activaStockBajoAlRegistrarSalida() {
        LoteMedicamento lote = loteConVencimientoFuturo(10, 5);

        lote.registrarSalida(6);

        assertEquals(4, lote.getStock());
        assertEquals(EstadoLote.STOCK_BAJO, lote.getEstado());
    }

    @Test
    void bloqueaSalidaDeLoteVencido() {
        LoteMedicamento lote = new LoteMedicamento(
                "L-003", "Medicamento", LocalDate.now().minusMonths(2),
                LocalDate.now().minusDays(1), 10, 2);

        assertThrows(ReglaNegocioException.class, () -> lote.registrarSalida(1));
        assertEquals(10, lote.getStock());
        assertEquals(EstadoLote.VENCIDO, lote.getEstado());
    }

    @Test
    void casoDeUsoOrquestaRepositorioYMovimiento() {
        LoteRepositoryPort repositorio = org.mockito.Mockito.mock(LoteRepositoryPort.class);
        LoteMedicamento lote = loteConVencimientoFuturo(10, 2);
        when(repositorio.buscarPorNumeroLote("L-004")).thenReturn(lote);
        RegistrarMovimientoUseCase casoDeUso = new RegistrarMovimientoUseCase(repositorio);

        casoDeUso.registrarSalida("L-004", 3);

        assertEquals(7, lote.getStock());
        verify(repositorio).buscarPorNumeroLote("L-004");
        verify(repositorio).guardar(lote);
    }

    private LoteMedicamento loteConVencimientoFuturo(int stock, int stockMinimo) {
        return new LoteMedicamento(
                "L-001", "Medicamento", LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6), stock, stockMinimo);
    }
}
