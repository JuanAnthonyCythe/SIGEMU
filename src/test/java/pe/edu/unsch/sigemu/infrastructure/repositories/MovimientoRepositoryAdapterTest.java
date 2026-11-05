package pe.edu.unsch.sigemu.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.infrastructure.database.SpringDataMovimientoRepository;
import pe.edu.unsch.sigemu.infrastructure.database.entities.MovimientoEntity;
import pe.edu.unsch.sigemu.infrastructure.database.mappers.MovimientoMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - MovimientoRepositoryAdapter")
class MovimientoRepositoryAdapterTest {

    @Mock
    private SpringDataMovimientoRepository springDataRepository;

    private MovimientoMapper mapper;
    private MovimientoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new MovimientoMapper();
        adapter = new MovimientoRepositoryAdapter(springDataRepository, mapper);
    }

    @Test
    @DisplayName("registrar guarda la entidad en Spring Data y retorna el dominio con ID generado")
    void debeRegistrarMovimiento() {
        LocalDateTime ahora = LocalDateTime.now();
        MovimientoInventario movimiento = new MovimientoInventario(
                "LOT-01",
                "INGRESO",
                50,
                150,
                ahora,
                "Llegada de proveedor"
        );

        MovimientoEntity guardada = new MovimientoEntity(
                1L,
                "LOT-01",
                "INGRESO",
                50,
                150,
                ahora,
                "Llegada de proveedor"
        );

        when(springDataRepository.save(any(MovimientoEntity.class))).thenReturn(guardada);

        MovimientoInventario resultado = adapter.registrar(movimiento);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("LOT-01", resultado.getNumeroLote());
        assertEquals(50, resultado.getCantidad());
        assertEquals(150, resultado.getStockResultante());

        ArgumentCaptor<MovimientoEntity> captor = ArgumentCaptor.forClass(MovimientoEntity.class);
        verify(springDataRepository).save(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals("LOT-01", captor.getValue().getNumeroLote());
    }

    @Test
    @DisplayName("registrar lanza IllegalArgumentException si movimiento es null")
    void debeLanzarExcepcionSiMovimientoEsNull() {
        assertThrows(IllegalArgumentException.class, () -> adapter.registrar(null));
    }

    @Test
    @DisplayName("listarPorLote retorna lista cronológica de movimientos mapeados")
    void debeListarPorLote() {
        LocalDateTime ahora = LocalDateTime.now();
        MovimientoEntity e1 = new MovimientoEntity(1L, "LOT-01", "INGRESO", 100, 100, ahora.minusHours(2), "Inicial");
        MovimientoEntity e2 = new MovimientoEntity(2L, "LOT-01", "SALIDA", 20, 80, ahora.minusHours(1), "Dispensación");

        when(springDataRepository.findByNumeroLoteOrderByFechaHoraAscIdAsc("LOT-01")).thenReturn(List.of(e1, e2));

        List<MovimientoInventario> resultado = adapter.listarPorLote("LOT-01");

        assertEquals(2, resultado.size());
        assertEquals("INGRESO", resultado.get(0).getTipoMovimiento());
        assertEquals(80, resultado.get(1).getStockResultante());
        verify(springDataRepository).findByNumeroLoteOrderByFechaHoraAscIdAsc("LOT-01");
    }

    @Test
    @DisplayName("listarPorLote retorna lista vacía si numeroLote es null o en blanco")
    void debeRetornarListaVaciaSiNumeroLoteEsInvalido() {
        assertTrue(adapter.listarPorLote(null).isEmpty());
        assertTrue(adapter.listarPorLote("   ").isEmpty());
    }
}
