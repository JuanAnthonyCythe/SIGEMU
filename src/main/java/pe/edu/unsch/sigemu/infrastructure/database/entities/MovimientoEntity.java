package pe.edu.unsch.sigemu.infrastructure.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_kardex")
public class MovimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lote", nullable = false)
    private String numeroLote;

    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "stock_resultante", nullable = false)
    private int stockResultante;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    public MovimientoEntity() {
    }

    public MovimientoEntity(String numeroLote, String tipoMovimiento, int cantidad,
                            int stockResultante, LocalDateTime fechaHora, String motivo) {
        this(null, numeroLote, tipoMovimiento, cantidad, stockResultante, fechaHora, motivo);
    }

    public MovimientoEntity(Long id, String numeroLote, String tipoMovimiento, int cantidad,
                            int stockResultante, LocalDateTime fechaHora, String motivo) {
        this.id = id;
        this.numeroLote = numeroLote;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.stockResultante = stockResultante;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStockResultante() {
        return stockResultante;
    }

    public void setStockResultante(int stockResultante) {
        this.stockResultante = stockResultante;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
