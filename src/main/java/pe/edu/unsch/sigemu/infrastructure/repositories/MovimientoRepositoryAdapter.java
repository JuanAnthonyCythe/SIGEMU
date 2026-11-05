package pe.edu.unsch.sigemu.infrastructure.repositories;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.domain.repositories.MovimientoRepositoryPort;
import pe.edu.unsch.sigemu.infrastructure.database.SpringDataMovimientoRepository;
import pe.edu.unsch.sigemu.infrastructure.database.entities.MovimientoEntity;
import pe.edu.unsch.sigemu.infrastructure.database.mappers.MovimientoMapper;

@Repository
@Transactional
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final SpringDataMovimientoRepository springDataMovimientoRepository;
    private final MovimientoMapper movimientoMapper;

    public MovimientoRepositoryAdapter(SpringDataMovimientoRepository springDataMovimientoRepository,
                                       MovimientoMapper movimientoMapper) {
        this.springDataMovimientoRepository = Objects.requireNonNull(springDataMovimientoRepository,
                "SpringDataMovimientoRepository es obligatorio");
        this.movimientoMapper = Objects.requireNonNull(movimientoMapper,
                "MovimientoMapper es obligatorio");
    }

    @Override
    public MovimientoInventario registrar(MovimientoInventario movimiento) {
        if (movimiento == null) {
            throw new IllegalArgumentException("El movimiento a registrar no puede ser nulo");
        }

        MovimientoEntity entity = movimientoMapper.toEntity(movimiento);
        MovimientoEntity guardada = springDataMovimientoRepository.save(entity);
        return movimientoMapper.toDomain(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarPorLote(String numeroLote) {
        if (numeroLote == null || numeroLote.isBlank()) {
            return List.of();
        }

        return springDataMovimientoRepository.findByNumeroLoteOrderByFechaHoraAscIdAsc(numeroLote)
                .stream()
                .map(movimientoMapper::toDomain)
                .toList();
    }
}
