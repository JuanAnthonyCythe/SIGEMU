package pe.edu.unsch.sigemu.infrastructure.database.mappers;

import org.springframework.stereotype.Component;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.infrastructure.database.entities.MovimientoEntity;

@Component
public class MovimientoMapper {

    public MovimientoInventario toDomain(MovimientoEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MovimientoInventario(
                entity.getId(),
                entity.getNumeroLote(),
                entity.getTipoMovimiento(),
                entity.getCantidad(),
                entity.getStockResultante(),
                entity.getFechaHora(),
                entity.getMotivo()
        );
    }

    public MovimientoEntity toEntity(MovimientoInventario domain) {
        if (domain == null) {
            return null;
        }

        return new MovimientoEntity(
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
