package pe.edu.unsch.sigemu.infrastructure.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;

@Entity
@Table(name = "lotes")
public class LoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lote", nullable = false, unique = true)
    private String numeroLote;

    @Column(name = "medicamento", nullable = false)
    private String medicamento;

    @Column(name = "fecha_fabricacion", nullable = false)
    private LocalDate fechaFabricacion;

    @Column(name = "fecha_caducidad", nullable = false)
    private LocalDate fechaCaducidad;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoLote estado;

    public LoteEntity() {
    }

    public LoteEntity(String numeroLote, String medicamento, LocalDate fechaFabricacion,
                      LocalDate fechaCaducidad, int stock, int stockMinimo, EstadoLote estado) {
        this(null, numeroLote, medicamento, fechaFabricacion, fechaCaducidad, stock, stockMinimo, estado);
    }

    public LoteEntity(Long id, String numeroLote, String medicamento, LocalDate fechaFabricacion,
                      LocalDate fechaCaducidad, int stock, int stockMinimo, EstadoLote estado) {
        this.id = id;
        this.numeroLote = numeroLote;
        this.medicamento = medicamento;
        this.fechaFabricacion = fechaFabricacion;
        this.fechaCaducidad = fechaCaducidad;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.estado = estado;
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

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public LocalDate getFechaFabricacion() {
        return fechaFabricacion;
    }

    public void setFechaFabricacion(LocalDate fechaFabricacion) {
        this.fechaFabricacion = fechaFabricacion;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public EstadoLote getEstado() {
        return estado;
    }

    public void setEstado(EstadoLote estado) {
        this.estado = estado;
    }
}
