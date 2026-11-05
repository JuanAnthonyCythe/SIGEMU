package pe.edu.unsch.sigemu.application.usecases;

import java.time.LocalDateTime;
import java.util.Objects;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.repositories.MovimientoRepositoryPort;
import pe.edu.unsch.sigemu.domain.rules.LoteNoEncontradoException;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

public class RegistrarMovimientoUseCase {

    private final LoteRepositoryPort loteRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    public RegistrarMovimientoUseCase(LoteRepositoryPort loteRepository) {
        this(loteRepository, null);
    }

    public RegistrarMovimientoUseCase(LoteRepositoryPort loteRepository,
                                      MovimientoRepositoryPort movimientoRepository) {
        this.loteRepository = Objects.requireNonNull(loteRepository, "El repositorio de lotes es obligatorio");
        this.movimientoRepository = movimientoRepository;
    }

    public LoteMedicamento registrarMovimiento(String numeroLote, String tipo, int cantidad) {
        return registrarMovimiento(numeroLote, tipo, cantidad, null);
    }

    public LoteMedicamento registrarMovimiento(String numeroLote, String tipo, int cantidad, String motivo) {
        if (tipo == null || tipo.isBlank()) {
            throw new ReglaNegocioException("El tipo de movimiento es obligatorio (INGRESO o SALIDA)");
        }

        String tipoNormalizado = tipo.trim().toUpperCase();
        return switch (tipoNormalizado) {
            case "INGRESO" -> registrarIngreso(numeroLote, cantidad, motivo);
            case "SALIDA" -> registrarSalida(numeroLote, cantidad, motivo);
            default -> throw new ReglaNegocioException("Tipo de movimiento no válido: " + tipo + ". Debe ser INGRESO o SALIDA");
        };
    }

    public LoteMedicamento registrarIngreso(String numeroLote, int cantidad) {
        return registrarIngreso(numeroLote, cantidad, null);
    }

    public LoteMedicamento registrarIngreso(String numeroLote, int cantidad, String motivo) {
        LoteMedicamento lote = obtenerLote(numeroLote);
        lote.registrarIngreso(cantidad);
        loteRepository.guardar(lote);

        if (movimientoRepository != null) {
            String motivoFinal = (motivo == null || motivo.isBlank()) ? "Ingreso de stock" : motivo.trim();
            MovimientoInventario movimiento = new MovimientoInventario(
                    lote.getNumeroLote(),
                    "INGRESO",
                    cantidad,
                    lote.getStock(),
                    LocalDateTime.now(),
                    motivoFinal
            );
            movimientoRepository.registrar(movimiento);
        }

        return lote;
    }

    public LoteMedicamento registrarSalida(String numeroLote, int cantidad) {
        return registrarSalida(numeroLote, cantidad, null);
    }

    public LoteMedicamento registrarSalida(String numeroLote, int cantidad, String motivo) {
        LoteMedicamento lote = obtenerLote(numeroLote);
        lote.registrarSalida(cantidad);
        loteRepository.guardar(lote);

        if (movimientoRepository != null) {
            String motivoFinal = (motivo == null || motivo.isBlank()) ? "Salida de stock" : motivo.trim();
            MovimientoInventario movimiento = new MovimientoInventario(
                    lote.getNumeroLote(),
                    "SALIDA",
                    cantidad,
                    lote.getStock(),
                    LocalDateTime.now(),
                    motivoFinal
            );
            movimientoRepository.registrar(movimiento);
        }

        return lote;
    }

    private LoteMedicamento obtenerLote(String numeroLote) {
        if (numeroLote == null || numeroLote.isBlank()) {
            throw new LoteNoEncontradoException("El número de lote es obligatorio");
        }
        LoteMedicamento lote = loteRepository.buscarPorNumeroLote(numeroLote);
        if (lote == null) {
            throw new LoteNoEncontradoException("No existe el lote: " + numeroLote);
        }
        return lote;
    }
}
