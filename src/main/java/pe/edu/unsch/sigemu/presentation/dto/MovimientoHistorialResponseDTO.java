package pe.edu.unsch.sigemu.presentation.dto;

import java.time.LocalDateTime;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;

public record MovimientoHistorialResponseDTO(
        Long id,
        String numeroLote,
        String tipoMovimiento,
        int cantidad,
        int stockResultante,
        LocalDateTime fechaHora,
        String motivo
) {
    public static MovimientoHistorialResponseDTO fromDomain(MovimientoInventario domain) {
        if (domain == null) {
            return null;
        }

        return new MovimientoHistorialResponseDTO(
                domain.getId(),
                domain.getNumeroLote(),
                domain.getTipoMovimiento(),
                domain.getCantidad(),
                domain.getStockResultante(),
                domain.getFechaHora(),
                domain.getMotivo()
        );
    }
}
