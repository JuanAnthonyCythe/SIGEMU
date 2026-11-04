package pe.edu.unsch.sigemu.domain.rules;

import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;

public interface PoliticaAlertaStock {

    EstadoLote evaluar(LoteMedicamento lote);
}
