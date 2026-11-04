package pe.edu.unsch.sigemu.application.usecases;

import java.util.Objects;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

public class RegistrarMovimientoUseCase {

    private final LoteRepositoryPort loteRepository;

    public RegistrarMovimientoUseCase(LoteRepositoryPort loteRepository) {
        this.loteRepository = Objects.requireNonNull(loteRepository, "El repositorio de lotes es obligatorio");
    }

    public void registrarIngreso(String numeroLote, int cantidad) {
        LoteMedicamento lote = obtenerLote(numeroLote);
        lote.registrarIngreso(cantidad);
        loteRepository.guardar(lote);
    }

    public void registrarSalida(String numeroLote, int cantidad) {
        LoteMedicamento lote = obtenerLote(numeroLote);
        lote.registrarSalida(cantidad);
        loteRepository.guardar(lote);
    }

    private LoteMedicamento obtenerLote(String numeroLote) {
        LoteMedicamento lote = loteRepository.buscarPorNumeroLote(numeroLote);
        if (lote == null) {
            throw new ReglaNegocioException("No existe el lote: " + numeroLote);
        }
        return lote;
    }
}
