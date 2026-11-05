package pe.edu.unsch.sigemu.infrastructure.scheduling;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.edu.unsch.sigemu.application.usecases.ActualizarEstadoCaducidadUseCase;

@Component
public class CaducidadScheduler {

    private static final Logger log = LoggerFactory.getLogger(CaducidadScheduler.class);

    private final ActualizarEstadoCaducidadUseCase actualizarEstadoCaducidadUseCase;

    public CaducidadScheduler(ActualizarEstadoCaducidadUseCase actualizarEstadoCaducidadUseCase) {
        this.actualizarEstadoCaducidadUseCase = Objects.requireNonNull(actualizarEstadoCaducidadUseCase,
                "ActualizarEstadoCaducidadUseCase es obligatorio");
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void revisarLotesVencidosDiario() {
        log.info("Iniciando revisión automática de caducidad de lotes...");
        int actualizados = actualizarEstadoCaducidadUseCase.ejecutarRevision();
        log.info("Revisión de caducidad finalizada con éxito. Total de lotes actualizados a VENCIDO: {}", actualizados);
    }
}
