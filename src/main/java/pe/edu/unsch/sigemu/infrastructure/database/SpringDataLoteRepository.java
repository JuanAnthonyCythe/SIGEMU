package pe.edu.unsch.sigemu.infrastructure.database;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unsch.sigemu.infrastructure.database.entities.LoteEntity;

public interface SpringDataLoteRepository extends JpaRepository<LoteEntity, Long> {

    Optional<LoteEntity> findByNumeroLote(String numeroLote);

    List<LoteEntity> findByMedicamentoContainingIgnoreCase(String medicamento);
}
