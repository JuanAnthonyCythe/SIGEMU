package pe.edu.unsch.sigemu.domain.repositories;

import java.util.List;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;

public interface MovimientoRepositoryPort {

    MovimientoInventario registrar(MovimientoInventario movimiento);

    List<MovimientoInventario> listarPorLote(String numeroLote);
}
