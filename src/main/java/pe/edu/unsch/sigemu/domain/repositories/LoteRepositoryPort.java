package pe.edu.unsch.sigemu.domain.repositories;

import java.util.List;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;

public interface LoteRepositoryPort {

    LoteMedicamento buscarPorNumeroLote(String numeroLote);

    void guardar(LoteMedicamento lote);

    List<LoteMedicamento> listarTodos();

    List<LoteMedicamento> buscarPorMedicamento(String medicamento);
}
