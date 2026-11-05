package pe.edu.unsch.sigemu.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - JwtService")
class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "claveDePruebaSuperSeguraDeMasDe256BitsParaJwtUnitTestingUNSCH2026!";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, 3600000L); // 1 hora
    }

    @Test
    @DisplayName("generarToken genera un JWT válido y permite extraer username y rol")
    void debeGenerarYExtraerDatosDeToken() {
        String token = jwtService.generarToken("admin", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("admin", jwtService.extraerUsername(token));
        assertEquals("ADMIN", jwtService.extraerRole(token));
        assertTrue(jwtService.esTokenValido(token, "admin"));
        assertFalse(jwtService.esTokenValido(token, "otroUsuario"));
    }

    @Test
    @DisplayName("esTokenValido retorna false ante token alterado o malformado")
    void debeRechazarTokenInvalido() {
        assertFalse(jwtService.esTokenValido("token.invalido.malformado", "admin"));
        assertFalse(jwtService.esTokenValido(null, "admin"));
    }

    @Test
    @DisplayName("esTokenValido retorna false ante token expirado")
    void debeRechazarTokenExpirado() {
        JwtService servicioExpirado = new JwtService(secret, -1000L);
        String tokenExpirado = servicioExpirado.generarToken("admin", "ADMIN");

        assertFalse(jwtService.esTokenValido(tokenExpirado, "admin"));
    }
}
