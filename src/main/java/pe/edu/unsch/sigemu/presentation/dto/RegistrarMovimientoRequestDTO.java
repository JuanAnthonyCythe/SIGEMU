package pe.edu.unsch.sigemu.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RegistrarMovimientoRequestDTO(
        @NotBlank(message = "El número de lote es obligatorio")
        String numeroLote,

        @NotBlank(message = "El tipo de movimiento es obligatorio (INGRESO o SALIDA)")
        String tipo,

        @Positive(message = "La cantidad debe ser mayor que cero")
        int cantidad,

        String motivo
) {
    public RegistrarMovimientoRequestDTO(String numeroLote, String tipo, int cantidad) {
        this(numeroLote, tipo, cantidad, null);
    }
}
