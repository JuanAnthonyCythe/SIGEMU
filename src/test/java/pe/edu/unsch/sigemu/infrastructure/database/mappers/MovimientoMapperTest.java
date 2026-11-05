package pe.edu.unsch.sigemu.infrastructure.database.mappers;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.infrastructure.database.entities.MovimientoEntity;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - MovimientoMapper")
class MovimientoMapperTest {

    private MovimientoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MovimientoMapper();
    }

    @Test
    @DisplayName("Debe mapear MovimientoEntity a MovimientoInventario correctamente")
    void debeMapearEntityADomain() {
        LocalDateTime ahora = LocalDateTime.now();
        MovimientoEntity entity = new MovimientoEntity(
                10L,
                "LOT-01",
                "INGRESO",
                20,
                50,
                ahora,
                "Compra regular"
        );

        MovimientoInventario domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(10L, domain.getId());
        assertEquals("LOT-01", domain.getNumeroLote());
        assertEquals("INGRESO", domain.getTipoMovimiento());
        assertEquals(20, domain.getCantidad());
        assertEquals(50, domain.getStockResultante());
        assertEquals(ahora, domain.getFechaHora());
        assertEquals("Compra regular", domain.getMotivo());
    }

    @Test
    @DisplayName("Debe retornar null cuando MovimientoEntity es null")
    void debeRetornarNullSiEntityEsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Debe mapear MovimientoInventario a MovimientoEntity correctamente")
    void debeMapearDomainAEntity() {
        LocalDateTime ahora = LocalDateTime.now();
        MovimientoInventario domain = new MovimientoInventario(
                5L,
                "LOT-02",
                "SALIDA",
                10,
                30,
                ahora,
                "Dispensación receta"
        );

        MovimientoEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(5L, entity.getId());
        assertEquals("LOT-02", entity.getNumeroLote());
        assertEquals("SALIDA", entity.getTipoMovimiento());
        assertEquals(10, entity.getCantidad());
        assertEquals(30, entity.getStockResultante());
        assertEquals(ahora, entity.getFechaHora());
        assertEquals("Dispensación receta", entity.getMotivo());
    }

    @Test
    @DisplayName("Debe retornar null cuando MovimientoInventario es null")
    void debeRetornarNullSiDomainEsNull() {
        assertNull(mapper.toEntity(null));
    }
}
