package pe.edu.unsch.sigemu.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.unsch.sigemu.presentation.dto.LoginRequestDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Integración - Spring Security & JWT")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/v1/lotes es público (permitAll) y no requiere token")
    void debePermitirAccesoPublicoAConsultaLotes() throws Exception {
        mockMvc.perform(get("/api/v1/lotes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login es público y permite autenticarse a admin y farmaceutico")
    void debePermitirLoginPublico() throws Exception {
        LoginRequestDTO adminLogin = new LoginRequestDTO("admin", "admin123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.rol").value("ADMIN"));

        LoginRequestDTO farmaLogin = new LoginRequestDTO("farmaceutico", "farma123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmaLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.rol").value("FARMACEUTICO"));
    }

    @Test
    @DisplayName("POST /api/v1/lotes sin token retorna 401 Unauthorized con formato JSON")
    void debeRechazarCreacionLoteSinToken() throws Exception {
        mockMvc.perform(post("/api/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("No Autorizado"))
                .andExpect(jsonPath("$.mensaje").value("Token JWT faltante, inválido o expirado"));
    }

    @Test
    @DisplayName("POST /api/v1/lotes con token de FARMACEUTICO retorna 403 Forbidden")
    void debeDenegarCreacionLoteAFarmaceutico() throws Exception {
        String tokenFarma = jwtService.generarToken("farmaceutico", "FARMACEUTICO");

        mockMvc.perform(post("/api/v1/lotes")
                        .header("Authorization", "Bearer " + tokenFarma)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acceso Denegado"))
                .andExpect(jsonPath("$.mensaje").value("No cuenta con los permisos o rol requeridos para esta operación"));
    }

    @Test
    @DisplayName("POST /api/v1/lotes con token de ADMIN permite crear lote")
    void debePermitirCreacionLoteAAdmin() throws Exception {
        String tokenAdmin = jwtService.generarToken("admin", "ADMIN");
        String numeroLote = "LOT-TEST-SEC-" + System.currentTimeMillis();

        mockMvc.perform(post("/api/v1/lotes")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "numeroLote", numeroLote,
                                "medicamento", "Ibuprofeno 400mg",
                                "stockInicial", 50,
                                "fechaCaducidad", "2027-12-31"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroLote").value(numeroLote))
                .andExpect(jsonPath("$.medicamento").value("Ibuprofeno 400mg"));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/movimientos sin token retorna 401 Unauthorized")
    void debeRechazarMovimientoSinToken() throws Exception {
        mockMvc.perform(post("/api/v1/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("No Autorizado"));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/movimientos con token de FARMACEUTICO es autorizado")
    void debeAutorizarMovimientoAFarmaceutico() throws Exception {
        String tokenFarma = jwtService.generarToken("farmaceutico", "FARMACEUTICO");

        // Al estar autenticado con rol FARMACEUTICO, no da 401 ni 403; responde 400 por payload inválido de prueba
        mockMvc.perform(post("/api/v1/inventario/movimientos")
                        .header("Authorization", "Bearer " + tokenFarma)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Preflight CORS (OPTIONS) desde origen permitido (localhost:5173) debe retornar headers CORS")
    void debePermitirPreflightCorsDesdeOrigenPermitido() throws Exception {
        mockMvc.perform(options("/api/v1/lotes")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }
}
