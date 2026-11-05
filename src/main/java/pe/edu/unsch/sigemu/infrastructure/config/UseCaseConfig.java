package pe.edu.unsch.sigemu.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.unsch.sigemu.application.usecases.ActualizarEstadoCaducidadUseCase;
import pe.edu.unsch.sigemu.application.usecases.ConsultarKardexUseCase;
import pe.edu.unsch.sigemu.application.usecases.GestionLotesUseCase;
import pe.edu.unsch.sigemu.application.usecases.RegistrarMovimientoUseCase;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.repositories.MovimientoRepositoryPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public GestionLotesUseCase gestionLotesUseCase(LoteRepositoryPort loteRepositoryPort) {
        return new GestionLotesUseCase(loteRepositoryPort);
    }

    @Bean
    public RegistrarMovimientoUseCase registrarMovimientoUseCase(LoteRepositoryPort loteRepositoryPort,
                                                                MovimientoRepositoryPort movimientoRepositoryPort) {
        return new RegistrarMovimientoUseCase(loteRepositoryPort, movimientoRepositoryPort);
    }

    @Bean
    public ConsultarKardexUseCase consultarKardexUseCase(MovimientoRepositoryPort movimientoRepositoryPort,
                                                         LoteRepositoryPort loteRepositoryPort) {
        return new ConsultarKardexUseCase(movimientoRepositoryPort, loteRepositoryPort);
    }

    @Bean
    public ActualizarEstadoCaducidadUseCase actualizarEstadoCaducidadUseCase(LoteRepositoryPort loteRepositoryPort) {
        return new ActualizarEstadoCaducidadUseCase(loteRepositoryPort);
    }
}
