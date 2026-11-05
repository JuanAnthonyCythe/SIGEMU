package pe.edu.unsch.sigemu.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            final String username = jwtService.extraerUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.esTokenValido(jwt, username)) {
                    String role = jwtService.extraerRole(jwt);
                    if (role == null || role.isBlank()) {
                        role = "USER";
                    }
                    String cleanRole = role.replace("ROLE_", "").trim();

                    // Asignar explícitamente ambas autoridades (con y sin prefijo ROLE_)
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + cleanRole));
                    authorities.add(new SimpleGrantedAuthority(cleanRole));

                    UserDetails userDetails = null;
                    try {
                        userDetails = this.userDetailsService.loadUserByUsername(username);
                        if (userDetails != null && userDetails.getAuthorities() != null) {
                            for (GrantedAuthority ga : userDetails.getAuthorities()) {
                                if (!authorities.contains(ga)) {
                                    authorities.add(ga);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.debug("Usuario no encontrado en UserDetailsService, usando token directo: " + ex.getMessage());
                    }

                    Object principal = (userDetails != null) ? userDetails : username;
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.debug("Error procesando token JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
