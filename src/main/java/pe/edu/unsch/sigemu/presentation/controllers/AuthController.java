package pe.edu.unsch.sigemu.presentation.controllers;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.unsch.sigemu.infrastructure.security.JwtService;
import pe.edu.unsch.sigemu.presentation.dto.AuthResponseDTO;
import pe.edu.unsch.sigemu.presentation.dto.LoginRequestDTO;

@RestController
@Validated
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = Objects.requireNonNull(authenticationManager, "AuthenticationManager es obligatorio");
        this.jwtService = Objects.requireNonNull(jwtService, "JwtService es obligatorio");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.usuario(), dto.contrasenia())
        );

        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                .replace("ROLE_", "");

        String token = jwtService.generarToken(dto.usuario(), rol);

        AuthResponseDTO respuesta = new AuthResponseDTO(token, "Bearer", dto.usuario(), rol);
        return ResponseEntity.ok(respuesta);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> manejarBadCredentials(BadCredentialsException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", HttpStatus.UNAUTHORIZED.value());
        cuerpo.put("error", "No Autorizado");
        cuerpo.put("mensaje", "Credenciales incorrectas: usuario o contraseña no válidos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cuerpo);
    }
}
