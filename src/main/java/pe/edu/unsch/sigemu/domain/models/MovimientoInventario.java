package pe.edu.unsch.sigemu.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

public class MovimientoInventario {

    private final Long id;
    private final String numeroLote;
    private final String tipoMovimiento;
    private final int cantidad;
    private final int stockResultante;
    private final LocalDateTime fechaHora;
    private final String motivo;

    public MovimientoInventario(String numeroLote, String tipoMovimiento, int cantidad,
                                int stockResultante, LocalDateTime fechaHora, String motivo) {
        this(null, numeroLote, tipoMovimiento, cantidad, stockResultante, fechaHora, motivo);
    }

    public MovimientoInventario(Long id, String numeroLote, String tipoMovimiento, int cantidad,
                                int stockResultante, LocalDateTime fechaHora, String motivo) {
        this.id = id;
        this.numeroLote = requireText(numeroLote, "El número de lote es obligatorio");
        this.tipoMovimiento = requireText(tipoMovimiento, "El tipo de movimiento es obligatorio");
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad del movimiento debe ser mayor que cero");
        }
        if (stockResultante < 0) {
            throw new ReglaNegocioException("El stock resultante no puede ser negativo");
        }
        this.cantidad = cantidad;
        this.stockResultante = stockResultante;
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora del movimiento es obligatoria");
        this.motivo = requireText(motivo, "El motivo del movimiento es obligatorio");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ReglaNegocioException(message);
        }
        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockResultante() {
        return stockResultante;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }
}
