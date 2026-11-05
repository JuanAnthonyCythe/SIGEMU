package pe.edu.unsch.sigemu.domain.entities;

import java.time.LocalDate;
import java.util.Objects;
import pe.edu.unsch.sigemu.domain.rules.PoliticaAlertaEstandar;
import pe.edu.unsch.sigemu.domain.rules.PoliticaAlertaStock;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;

public class LoteMedicamento {

    private final Long id;
    private final String numeroLote;
    private final String medicamento;
    private final LocalDate fechaFabricacion;
    private final LocalDate fechaCaducidad;
    private final int stockMinimo;
    private final PoliticaAlertaStock politicaAlerta;
    private int stock;
    private EstadoLote estado;

    public LoteMedicamento(String numeroLote, String medicamento,
                           LocalDate fechaFabricacion, LocalDate fechaCaducidad,
                           int stock, int stockMinimo) {
        this(null, numeroLote, medicamento, fechaFabricacion, fechaCaducidad,
                stock, stockMinimo, new PoliticaAlertaEstandar());
    }

    public LoteMedicamento(String numeroLote, String medicamento,
                           LocalDate fechaFabricacion, LocalDate fechaCaducidad,
                           int stock, int stockMinimo, PoliticaAlertaStock politicaAlerta) {
        this(null, numeroLote, medicamento, fechaFabricacion, fechaCaducidad,
                stock, stockMinimo, politicaAlerta);
    }

    public LoteMedicamento(Long id, String numeroLote, String medicamento,
                           LocalDate fechaFabricacion, LocalDate fechaCaducidad,
                           int stock, int stockMinimo) {
        this(id, numeroLote, medicamento, fechaFabricacion, fechaCaducidad,
                stock, stockMinimo, (EstadoLote) null);
    }

    public LoteMedicamento(Long id, String numeroLote, String medicamento,
                           LocalDate fechaFabricacion, LocalDate fechaCaducidad,
                           int stock, int stockMinimo, PoliticaAlertaStock politicaAlerta) {
        this(id, numeroLote, medicamento, fechaFabricacion, fechaCaducidad,
                stock, stockMinimo, politicaAlerta, null);
    }

    public LoteMedicamento(Long id, String numeroLote, String medicamento,
                           LocalDate fechaFabricacion, LocalDate fechaCaducidad,
                           int stock, int stockMinimo, EstadoLote estado) {
        this(id, numeroLote, medicamento, fechaFabricacion, fechaCaducidad,
                stock, stockMinimo, new PoliticaAlertaEstandar(), estado);
    }

    public LoteMedicamento(Long id, String numeroLote, String medicamento,
                           LocalDate fechaFabricacion, LocalDate fechaCaducidad,
                           int stock, int stockMinimo, PoliticaAlertaStock politicaAlerta,
                           EstadoLote estado) {
        this.id = id;
        this.numeroLote = requireText(numeroLote, "El número de lote es obligatorio");
        this.medicamento = requireText(medicamento, "El medicamento es obligatorio");
        this.fechaFabricacion = Objects.requireNonNull(fechaFabricacion, "La fecha de fabricación es obligatoria");
        this.fechaCaducidad = Objects.requireNonNull(fechaCaducidad, "La fecha de caducidad es obligatoria");
        if (!fechaCaducidad.isAfter(fechaFabricacion)) {
            throw new ReglaNegocioException("La fecha de caducidad debe ser posterior a la fabricación");
        }
        if (stock < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }
        if (stockMinimo < 0) {
            throw new ReglaNegocioException("El stock mínimo no puede ser negativo");
        }
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.politicaAlerta = Objects.requireNonNull(politicaAlerta, "La política de alerta es obligatoria");
        this.estado = (estado != null) ? estado : this.politicaAlerta.evaluar(this);
    }

    public void registrarIngreso(int cantidad) {
        validarCantidad(cantidad);
        stock += cantidad;
        actualizarEstado();
    }

    public void registrarSalida(int cantidad) {
        validarCantidad(cantidad);
        if (estaVencido()) {
            throw new ReglaNegocioException("No se puede retirar producto de un lote vencido");
        }
        if (cantidad > stock) {
            throw new ReglaNegocioException("El stock disponible es insuficiente");
        }
        stock -= cantidad;
        actualizarEstado();
    }

    public void marcarComoVencido() {
        this.estado = EstadoLote.VENCIDO;
    }

    private boolean estaVencido() {
        return fechaCaducidad.isBefore(LocalDate.now());
    }

    private void actualizarEstado() {
        estado = politicaAlerta.evaluar(this);
    }

    private static void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ReglaNegocioException(message);
        }
        return value;
    }

    public Long getId() { return id; }
    public String getNumeroLote() { return numeroLote; }
    public String getMedicamento() { return medicamento; }
    public LocalDate getFechaFabricacion() { return fechaFabricacion; }
    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public EstadoLote getEstado() { return estado; }
}
