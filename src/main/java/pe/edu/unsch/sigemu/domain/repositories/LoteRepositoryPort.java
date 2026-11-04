package pe.edu.unsch.sigemu.domain.repositories;

import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;

public interface LoteRepositoryPort {

    LoteMedicamento buscarPorNumeroLote(String numeroLote);

    void guardar(LoteMedicamento lote);
}
