package pe.edu.unsch.sigemu.infrastructure.scheduling;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsch.sigemu.application.usecases.ActualizarEstadoCaducidadUseCase;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - CaducidadScheduler")
class CaducidadSchedulerTest {

    @Mock
    private LoteRepositoryPort loteRepository;

    private ActualizarEstadoCaducidadUseCase useCase;
    private CaducidadScheduler scheduler;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarEstadoCaducidadUseCase(loteRepository);
        scheduler = new CaducidadScheduler(useCase);
    }

    @Test
    @DisplayName("revisarLotesVencidosDiario ejecuta la revisión de caducidad en el repositorio")
    void debeEjecutarRevisionProgramada() {
        when(loteRepository.listarTodos()).thenReturn(List.of());

        scheduler.revisarLotesVencidosDiario();

        verify(loteRepository).listarTodos();
    }
}
