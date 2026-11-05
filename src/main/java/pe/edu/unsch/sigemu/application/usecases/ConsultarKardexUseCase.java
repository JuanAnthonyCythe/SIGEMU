package pe.edu.unsch.sigemu.application.usecases;

import java.util.List;
import java.util.Objects;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.repositories.MovimientoRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.LoteNoEncontradoException;

public class ConsultarKardexUseCase {

    private final MovimientoRepositoryPort movimientoRepository;
    private final LoteRepositoryPort loteRepository;

    public ConsultarKardexUseCase(MovimientoRepositoryPort movimientoRepository,
                                  LoteRepositoryPort loteRepository) {
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository,
                "El repositorio de movimientos es obligatorio");
        this.loteRepository = Objects.requireNonNull(loteRepository,
                "El repositorio de lotes es obligatorio");
    }

    public List<MovimientoInventario> consultarPorLote(String numeroLote) {
        if (numeroLote == null || numeroLote.isBlank()) {
            throw new LoteNoEncontradoException("El número de lote es obligatorio");
        }

        if (loteRepository.buscarPorNumeroLote(numeroLote) == null) {
            throw new LoteNoEncontradoException("No existe el lote: " + numeroLote);
        }

        return movimientoRepository.listarPorLote(numeroLote);
    }
}
