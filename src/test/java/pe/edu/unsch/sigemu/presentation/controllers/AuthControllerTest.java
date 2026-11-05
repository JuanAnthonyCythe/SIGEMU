package pe.edu.unsch.sigemu.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.unsch.sigemu.infrastructure.security.JwtService;
import pe.edu.unsch.sigemu.presentation.dto.LoginRequestDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AuthController")
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    private JwtService jwtService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jwtService = new JwtService("claveSecretaJwtParaPruebasUnitariasUNSCH2026!DebeTenerMasDe256Bits", 3600000L);
        authController = new AuthController(authenticationManager, jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/login retorna 200 OK con token y rol para credenciales válidas")
    void debeAutenticarExitosamente() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("admin", "admin123");

        Authentication authExitosa = new UsernamePasswordAuthenticationToken(
                "admin", "admin123", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authExitosa);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario").value("admin"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login retorna 401 Unauthorized para credenciales incorrectas")
    void debeRechazarCredencialesIncorrectas() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("admin", "claveIncorrecta");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("No Autorizado"))
                .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas: usuario o contraseña no válidos"));
    }
}
