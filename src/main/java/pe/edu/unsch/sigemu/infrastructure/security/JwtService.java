package pe.edu.unsch.sigemu.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret:sigemuSecretKeyParaTokensJwtSprint3SeguridadUNSCH2026!ClaveSegura256Bits}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String username, String role) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerRole(String token) {
        return extraerClaims(token).get("role", String.class);
    }

    public boolean esTokenValido(String token, String username) {
        try {
            Claims claims = extraerClaims(token);
            boolean usernameCoincide = username != null && username.equals(claims.getSubject());
            boolean noExpirado = claims.getExpiration() != null && claims.getExpiration().after(new Date());
            return usernameCoincide && noExpirado;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
