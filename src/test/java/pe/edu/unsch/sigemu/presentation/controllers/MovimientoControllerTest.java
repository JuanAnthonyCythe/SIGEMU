package pe.edu.unsch.sigemu.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.unsch.sigemu.application.usecases.ConsultarKardexUseCase;
import pe.edu.unsch.sigemu.application.usecases.RegistrarMovimientoUseCase;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.domain.repositories.MovimientoRepositoryPort;
import pe.edu.unsch.sigemu.presentation.dto.RegistrarMovimientoRequestDTO;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias / Web - MovimientoController")
class MovimientoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private LoteRepositoryPort loteRepository;

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    private RegistrarMovimientoUseCase registrarMovimientoUseCase;
    private ConsultarKardexUseCase consultarKardexUseCase;
    private MovimientoController movimientoController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        registrarMovimientoUseCase = new RegistrarMovimientoUseCase(loteRepository, movimientoRepository);
        consultarKardexUseCase = new ConsultarKardexUseCase(movimientoRepository, loteRepository);
        movimientoController = new MovimientoController(registrarMovimientoUseCase, consultarKardexUseCase);

        mockMvc = MockMvcBuilders.standaloneSetup(movimientoController).build();
    }

    @Test
    @DisplayName("POST /api/v1/inventario/movimientos INGRESO retorna 200 OK, stock incrementado y registra kardex")
    void debeRegistrarIngresoExitosamente() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-01", "Paracetamol", fabricacion, caducidad, 20, 5
        );

        when(loteRepository.buscarPorNumeroLote("LOT-01")).thenReturn(lote);

        RegistrarMovimientoRequestDTO request = new RegistrarMovimientoRequestDTO("LOT-01", "INGRESO", 10, "Compra mensual");

        mockMvc.perform(post("/api/v1/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Movimiento registrado exitosamente"))
                .andExpect(jsonPath("$.numeroLote").value("LOT-01"))
                .andExpect(jsonPath("$.tipo").value("INGRESO"))
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.nuevoStock").value(30))
                .andExpect(jsonPath("$.nuevoEstado").value("VIGENTE"));

        verify(loteRepository).guardar(lote);
        verify(movimientoRepository).registrar(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/movimientos SALIDA retorna 200 OK y actualiza a STOCK_BAJO")
    void debeRegistrarSalidaConStockBajo() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-02", "Ibuprofeno", fabricacion, caducidad, 15, 10
        );

        when(loteRepository.buscarPorNumeroLote("LOT-02")).thenReturn(lote);

        RegistrarMovimientoRequestDTO request = new RegistrarMovimientoRequestDTO("LOT-02", "SALIDA", 10);

        mockMvc.perform(post("/api/v1/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroLote").value("LOT-02"))
                .andExpect(jsonPath("$.tipo").value("SALIDA"))
                .andExpect(jsonPath("$.nuevoStock").value(5))
                .andExpect(jsonPath("$.nuevoEstado").value("STOCK_BAJO"));

        verify(loteRepository).guardar(lote);
        verify(movimientoRepository).registrar(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/movimientos retorna 404 Not Found si el lote no existe")
    void debeRetornar404CuandoLoteNoExiste() throws Exception {
        when(loteRepository.buscarPorNumeroLote("NO-EXISTE")).thenReturn(null);

        RegistrarMovimientoRequestDTO request = new RegistrarMovimientoRequestDTO("NO-EXISTE", "SALIDA", 5);

        mockMvc.perform(post("/api/v1/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Lote No Encontrado"))
                .andExpect(jsonPath("$.mensaje").value("No existe el lote: NO-EXISTE"));

        verify(loteRepository, never()).guardar(any());
        verify(movimientoRepository, never()).registrar(any());
    }

    @Test
    @DisplayName("POST /api/v1/inventario/movimientos retorna 400 Bad Request si el stock es insuficiente")
    void debeRetornar400CuandoStockInsuficiente() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                "LOT-03", "Amoxicilina", fabricacion, caducidad, 5, 2
        );

        when(loteRepository.buscarPorNumeroLote("LOT-03")).thenReturn(lote);

        RegistrarMovimientoRequestDTO request = new RegistrarMovimientoRequestDTO("LOT-03", "SALIDA", 20);

        mockMvc.perform(post("/api/v1/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Regla de Negocio"))
                .andExpect(jsonPath("$.mensaje").value("El stock disponible es insuficiente"));

        verify(loteRepository, never()).guardar(any());
        verify(movimientoRepository, never()).registrar(any());
    }

    @Test
    @DisplayName("POST /api/v1/inventario/movimientos retorna 400 Bad Request si tipo es desconocido")
    void debeRetornar400CuandoTipoInvalido() throws Exception {
        RegistrarMovimientoRequestDTO request = new RegistrarMovimientoRequestDTO("LOT-01", "DESCONOCIDO", 5);

        mockMvc.perform(post("/api/v1/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Regla de Negocio"));
    }

    @Test
    @DisplayName("GET /api/v1/inventario/movimientos/lote/{numeroLote} retorna 200 OK con lista cronológica de movimientos")
    void debeConsultarKardexExitosamente() throws Exception {
        String numeroLote = "LOT-AUDIT";
        LoteMedicamento lote = new LoteMedicamento(
                numeroLote, "Amoxicilina 500mg", LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6), 100, 20
        );

        LocalDateTime t1 = LocalDateTime.now().minusHours(2);
        LocalDateTime t2 = LocalDateTime.now().minusHours(1);

        MovimientoInventario m1 = new MovimientoInventario(1L, numeroLote, "INGRESO", 100, 100, t1, "Stock inicial");
        MovimientoInventario m2 = new MovimientoInventario(2L, numeroLote, "SALIDA", 10, 90, t2, "Receta médica");

        when(loteRepository.buscarPorNumeroLote(numeroLote)).thenReturn(lote);
        when(movimientoRepository.listarPorLote(numeroLote)).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/v1/inventario/movimientos/lote/" + numeroLote))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipoMovimiento").value("INGRESO"))
                .andExpect(jsonPath("$[0].stockResultante").value(100))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].tipoMovimiento").value("SALIDA"))
                .andExpect(jsonPath("$[1].stockResultante").value(90));

        verify(loteRepository).buscarPorNumeroLote(numeroLote);
        verify(movimientoRepository).listarPorLote(numeroLote);
    }

    @Test
    @DisplayName("GET /api/v1/inventario/movimientos/lote/{numeroLote} retorna 404 Not Found cuando el lote no existe")
    void debeRetornar404AlConsultarKardexSiLoteNoExiste() throws Exception {
        when(loteRepository.buscarPorNumeroLote("INEXISTENTE")).thenReturn(null);

        mockMvc.perform(get("/api/v1/inventario/movimientos/lote/INEXISTENTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Lote No Encontrado"));

        verify(movimientoRepository, never()).listarPorLote(any());
    }
}
