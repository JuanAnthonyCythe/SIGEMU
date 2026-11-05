package pe.edu.unsch.sigemu.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
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
import pe.edu.unsch.sigemu.application.usecases.GestionLotesUseCase;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.repositories.LoteRepositoryPort;
import pe.edu.unsch.sigemu.presentation.dto.CrearLoteRequestDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias / Web - LoteController")
class LoteControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private LoteRepositoryPort loteRepository;

    private GestionLotesUseCase gestionLotesUseCase;

    private LoteController loteController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        gestionLotesUseCase = new GestionLotesUseCase(loteRepository);
        loteController = new LoteController(gestionLotesUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(loteController).build();
    }

    @Test
    @DisplayName("POST /api/v1/lotes retorna 201 Created con el LoteResponseDTO")
    void debeCrearLoteExitosamente() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        CrearLoteRequestDTO requestDto = new CrearLoteRequestDTO(
                "LOT-2026-001",
                "Amoxicilina 500mg",
                fabricacion,
                caducidad,
                100,
                20
        );

        LoteMedicamento creado = new LoteMedicamento(
                1L,
                "LOT-2026-001",
                "Amoxicilina 500mg",
                fabricacion,
                caducidad,
                100,
                20
        );

        when(loteRepository.buscarPorNumeroLote("LOT-2026-001"))
                .thenReturn(null)
                .thenReturn(creado);

        mockMvc.perform(post("/api/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/lotes/LOT-2026-001"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroLote").value("LOT-2026-001"))
                .andExpect(jsonPath("$.medicamento").value("Amoxicilina 500mg"))
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.stockMinimo").value(20))
                .andExpect(jsonPath("$.estado").value("VIGENTE"));

        verify(loteRepository).guardar(any(LoteMedicamento.class));
    }

    @Test
    @DisplayName("POST /api/v1/lotes retorna 400 Bad Request cuando regla de negocio falla")
    void debeRetornar400CuandoFallaReglaNegocio() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        CrearLoteRequestDTO requestDto = new CrearLoteRequestDTO(
                "LOT-DUPLICADO",
                "Amoxicilina",
                fabricacion,
                caducidad,
                50,
                10
        );

        LoteMedicamento existente = new LoteMedicamento(
                "LOT-DUPLICADO",
                "Amoxicilina",
                fabricacion,
                caducidad,
                50,
                10
        );

        when(loteRepository.buscarPorNumeroLote("LOT-DUPLICADO")).thenReturn(existente);

        mockMvc.perform(post("/api/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Regla de Negocio"))
                .andExpect(jsonPath("$.mensaje").value("Ya existe un lote con el número: LOT-DUPLICADO"));

        verify(loteRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("GET /api/v1/lotes retorna 200 OK con lista de lotes")
    void debeListarTodosLosLotes() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                1L, "LOT-01", "Paracetamol", fabricacion, caducidad, 40, 10
        );

        when(loteRepository.listarTodos()).thenReturn(List.of(lote));

        mockMvc.perform(get("/api/v1/lotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numeroLote").value("LOT-01"));
    }

    @Test
    @DisplayName("GET /api/v1/lotes/{numeroLote} retorna 200 OK si existe")
    void debeBuscarPorNumeroLoteExistente() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                2L, "LOT-02", "Ibuprofeno", fabricacion, caducidad, 30, 5
        );

        when(loteRepository.buscarPorNumeroLote("LOT-02")).thenReturn(lote);

        mockMvc.perform(get("/api/v1/lotes/LOT-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.numeroLote").value("LOT-02"))
                .andExpect(jsonPath("$.medicamento").value("Ibuprofeno"));
    }

    @Test
    @DisplayName("GET /api/v1/lotes/{numeroLote} retorna 404 Not Found si no existe")
    void debeRetornar404SiNoExiste() throws Exception {
        when(loteRepository.buscarPorNumeroLote("NO-EXISTE")).thenReturn(null);

        mockMvc.perform(get("/api/v1/lotes/NO-EXISTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/lotes/buscar?medicamento=... retorna 200 OK con coincidencias")
    void debeBuscarPorMedicamento() throws Exception {
        LocalDate fabricacion = LocalDate.now().minusMonths(1);
        LocalDate caducidad = LocalDate.now().plusMonths(6);

        LoteMedicamento lote = new LoteMedicamento(
                3L, "LOT-03", "Amoxicilina Jarabe", fabricacion, caducidad, 25, 5
        );

        when(loteRepository.buscarPorMedicamento("amox")).thenReturn(List.of(lote));

        mockMvc.perform(get("/api/v1/lotes/buscar").param("medicamento", "amox"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].medicamento").value("Amoxicilina Jarabe"));
    }
}
