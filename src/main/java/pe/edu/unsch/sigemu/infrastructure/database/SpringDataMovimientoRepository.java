package pe.edu.unsch.sigemu.infrastructure.database;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unsch.sigemu.infrastructure.database.entities.MovimientoEntity;

public interface SpringDataMovimientoRepository extends JpaRepository<MovimientoEntity, Long> {

    List<MovimientoEntity> findByNumeroLoteOrderByFechaHoraAscIdAsc(String numeroLote);
}
