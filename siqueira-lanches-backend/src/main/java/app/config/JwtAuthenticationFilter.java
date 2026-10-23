package app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceGenerator jwtService;

    public JwtAuthenticationFilter(JwtServiceGenerator jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.equals("/api/usuarios/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                String email = jwtService.getUsernameFromToken(token);
                String role = jwtService.getRoleFromToken(token);

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singleton(new SimpleGrantedAuthority(role))
                );

                System.out.println("✅ JwtAuthenticationFilter: Token válido! Setando role: " + role);
                System.out.println("✅ JwtAuthenticationFilter: Authentication setado: " + auth);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("❌ JwtAuthenticationFilter: Token inválido.");
            }
        } else {
            System.out.println("❌ JwtAuthenticationFilter: Authorization header ausente ou mal formatado.");
        }

        chain.doFilter(request, response);
    }
}
