package pe.edu.unsch.sigemu.infrastructure.repositories;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.infrastructure.database.SpringDataLoteRepository;
import pe.edu.unsch.sigemu.infrastructure.database.entities.LoteEntity;
import pe.edu.unsch.sigemu.infrastructure.database.mappers.LoteMapper;

@Repository
@Transactional
public class LoteRepositoryAdapter implements LoteRepositoryPort {

    private final SpringDataLoteRepository springDataLoteRepository;
    private final LoteMapper loteMapper;

    public LoteRepositoryAdapter(SpringDataLoteRepository springDataLoteRepository,
                                 LoteMapper loteMapper) {
        this.springDataLoteRepository = Objects.requireNonNull(springDataLoteRepository,
                "SpringDataLoteRepository es obligatorio");
        this.loteMapper = Objects.requireNonNull(loteMapper,
                "LoteMapper es obligatorio");
    }

    @Override
    @Transactional(readOnly = true)
    public LoteMedicamento buscarPorNumeroLote(String numeroLote) {
        if (numeroLote == null || numeroLote.isBlank()) {
            return null;
        }

        return springDataLoteRepository.findByNumeroLote(numeroLote)
                .map(loteMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void guardar(LoteMedicamento lote) {
        if (lote == null) {
            throw new IllegalArgumentException("El lote a guardar no puede ser nulo");
        }

        Optional<LoteEntity> loteExistente = springDataLoteRepository.findByNumeroLote(lote.getNumeroLote());
        LoteEntity entity;

        if (loteExistente.isPresent()) {
            entity = loteExistente.get();
            entity.setMedicamento(lote.getMedicamento());
            entity.setFechaFabricacion(lote.getFechaFabricacion());
            entity.setFechaCaducidad(lote.getFechaCaducidad());
            entity.setStock(lote.getStock());
            entity.setStockMinimo(lote.getStockMinimo());
            entity.setEstado(lote.getEstado());
        } else {
            entity = loteMapper.toEntity(lote);
        }

        springDataLoteRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteMedicamento> listarTodos() {
        return springDataLoteRepository.findAll()
                .stream()
                .map(loteMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteMedicamento> buscarPorMedicamento(String medicamento) {
        if (medicamento == null || medicamento.isBlank()) {
            return List.of();
        }

        return springDataLoteRepository.findByMedicamentoContainingIgnoreCase(medicamento)
                .stream()
                .map(loteMapper::toDomain)
                .toList();
    }
}
