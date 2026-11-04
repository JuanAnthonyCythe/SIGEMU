package pe.edu.unsch.sigemu.domain.rules;

import java.time.LocalDate;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;

public class PoliticaAlertaEstandar implements PoliticaAlertaStock {

    @Override
    public EstadoLote evaluar(LoteMedicamento lote) {
        if (lote.getFechaCaducidad().isBefore(LocalDate.now())) {
            return EstadoLote.VENCIDO;
        }
        if (lote.getStock() == 0) {
            return EstadoLote.AGOTADO;
        }
        if (lote.getStock() <= lote.getStockMinimo()) {
            return EstadoLote.STOCK_BAJO;
        }
        return EstadoLote.VIGENTE;
    }
}
