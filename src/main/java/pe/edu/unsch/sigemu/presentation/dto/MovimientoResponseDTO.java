package pe.edu.unsch.sigemu.presentation.dto;

import pe.edu.unsch.sigemu.domain.entities.EstadoLote;

public record MovimientoResponseDTO(
        String mensaje,
        String numeroLote,
        String tipo,
        int cantidad,
        int nuevoStock,
        EstadoLote nuevoEstado
) {
}
