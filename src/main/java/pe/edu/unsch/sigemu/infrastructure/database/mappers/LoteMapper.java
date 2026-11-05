package pe.edu.unsch.sigemu.infrastructure.database.mappers;

import org.springframework.stereotype.Component;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.infrastructure.database.entities.LoteEntity;

@Component
public class LoteMapper {

    public LoteMedicamento toDomain(LoteEntity entity) {
        if (entity == null) {
            return null;
        }

        return new LoteMedicamento(
                entity.getId(),
                entity.getNumeroLote(),
                entity.getMedicamento(),
                entity.getFechaFabricacion(),
                entity.getFechaCaducidad(),
                entity.getStock(),
                entity.getStockMinimo(),
                entity.getEstado()
        );
    }

    public LoteEntity toEntity(LoteMedicamento domain) {
        if (domain == null) {
            return null;
        }

        return new LoteEntity(
                domain.getId(),
                domain.getNumeroLote(),
                domain.getMedicamento(),
                domain.getFechaFabricacion(),
                domain.getFechaCaducidad(),
                domain.getStock(),
                domain.getStockMinimo(),
                domain.getEstado()
        );
    }
}
