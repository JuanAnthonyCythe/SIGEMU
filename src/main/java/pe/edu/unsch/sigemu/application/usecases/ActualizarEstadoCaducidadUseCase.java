package pe.edu.unsch.sigemu.application.usecases;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;

public class ActualizarEstadoCaducidadUseCase {

    private final LoteRepositoryPort loteRepository;

    public ActualizarEstadoCaducidadUseCase(LoteRepositoryPort loteRepository) {
        this.loteRepository = Objects.requireNonNull(loteRepository, "El repositorio de lotes es obligatorio");
    }

    public int ejecutarRevision() {
        LocalDate hoy = LocalDate.now();
        List<LoteMedicamento> todosLosLotes = loteRepository.listarTodos();

        int actualizados = 0;
        for (LoteMedicamento lote : todosLosLotes) {
            boolean esCandidato = lote.getEstado() == EstadoLote.VIGENTE || lote.getEstado() == EstadoLote.STOCK_BAJO;
            boolean estaCaducado = !lote.getFechaCaducidad().isAfter(hoy);

            if (esCandidato && estaCaducado) {
                lote.marcarComoVencido();
                loteRepository.guardar(lote);
                actualizados++;
            }
        }

        return actualizados;
    }
}
