package pe.edu.unsch.sigemu.application.usecases;

import java.util.List;
import java.util.Objects;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

public class GestionLotesUseCase {

    private final LoteRepositoryPort loteRepository;

    public GestionLotesUseCase(LoteRepositoryPort loteRepository) {
        this.loteRepository = Objects.requireNonNull(loteRepository, "El repositorio de lotes es obligatorio");
    }

    public LoteMedicamento crearLote(LoteMedicamento nuevoLote) {
        if (nuevoLote == null) {
            throw new ReglaNegocioException("El lote a crear es obligatorio");
        }

        if (loteRepository.buscarPorNumeroLote(nuevoLote.getNumeroLote()) != null) {
            throw new ReglaNegocioException("Ya existe un lote con el número: " + nuevoLote.getNumeroLote());
        }

        loteRepository.guardar(nuevoLote);
        return loteRepository.buscarPorNumeroLote(nuevoLote.getNumeroLote());
    }

    public List<LoteMedicamento> listarTodos() {
        return loteRepository.listarTodos();
    }

    public LoteMedicamento buscarPorNumero(String numeroLote) {
        if (numeroLote == null || numeroLote.isBlank()) {
            return null;
        }
        return loteRepository.buscarPorNumeroLote(numeroLote);
    }

    public List<LoteMedicamento> buscarPorMedicamento(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return List.of();
        }
        return loteRepository.buscarPorMedicamento(nombre);
    }
}
