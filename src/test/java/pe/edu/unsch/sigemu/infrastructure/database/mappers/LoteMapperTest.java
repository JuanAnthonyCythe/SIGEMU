package pe.edu.unsch.sigemu.infrastructure.database.mappers;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.infrastructure.database.entities.LoteEntity;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - LoteMapper")
class LoteMapperTest {

    private LoteMapper loteMapper;

    @BeforeEach
    void setUp() {
        loteMapper = new LoteMapper();
    }

    @Test
    @DisplayName("Debe mapear LoteEntity a LoteMedicamento correctamente con ID")
    void debeMapearEntityADomain() {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteEntity entity = new LoteEntity(
                1L,
                "LOT-2026-001",
                "Paracetamol 500mg",
                fabricacion,
                caducidad,
                100,
                20,
                EstadoLote.VIGENTE
        );

        LoteMedicamento domain = loteMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals("LOT-2026-001", domain.getNumeroLote());
        assertEquals("Paracetamol 500mg", domain.getMedicamento());
        assertEquals(fabricacion, domain.getFechaFabricacion());
        assertEquals(caducidad, domain.getFechaCaducidad());
        assertEquals(100, domain.getStock());
        assertEquals(20, domain.getStockMinimo());
        assertEquals(EstadoLote.VIGENTE, domain.getEstado());
    }

    @Test
    @DisplayName("Debe retornar null cuando LoteEntity es null al mapear a domain")
    void debeRetornarNullSiEntityEsNull() {
        assertNull(loteMapper.toDomain(null));
    }

    @Test
    @DisplayName("Debe mapear LoteMedicamento a LoteEntity correctamente")
    void debeMapearDomainAEntity() {
        LocalDate fabricacion = LocalDate.now().minusMonths(2);
        LocalDate caducidad = LocalDate.now().plusMonths(12);

        LoteMedicamento domain = new LoteMedicamento(
                "LOT-2026-002",
                "Amoxicilina 500mg",
                fabricacion,
                caducidad,
                50,
                10
        );

        LoteEntity entity = loteMapper.toEntity(domain);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("LOT-2026-002", entity.getNumeroLote());
        assertEquals("Amoxicilina 500mg", entity.getMedicamento());
        assertEquals(fabricacion, entity.getFechaFabricacion());
        assertEquals(caducidad, entity.getFechaCaducidad());
        assertEquals(50, entity.getStock());
        assertEquals(10, entity.getStockMinimo());
        assertEquals(EstadoLote.VIGENTE, entity.getEstado());
    }

    @Test
    @DisplayName("Debe mapear LoteMedicamento con ID a LoteEntity correctamente")
    void debeMapearDomainConIdAEntity() {
        LocalDate fabricacion = LocalDate.now().minusMonths(2);
        LocalDate caducidad = LocalDate.now().plusMonths(12);

        LoteMedicamento domain = new LoteMedicamento(
                55L,
                "LOT-2026-003",
                "Ibuprofeno",
                fabricacion,
                caducidad,
                60,
                10
        );

        LoteEntity entity = loteMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(55L, entity.getId());
        assertEquals("LOT-2026-003", entity.getNumeroLote());
    }

    @Test
    @DisplayName("Debe retornar null cuando LoteMedicamento es null al mapear a entity")
    void debeRetornarNullSiDomainEsNull() {
        assertNull(loteMapper.toEntity(null));
    }
}
