package com.intellimarket.api.auth.security;

import com.intellimarket.api.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Buscamos el encabezado "Authorization" que envía el cliente (Postman/Frontend)
        final String authHeader = request.getHeader("Authorization");

        // Si no hay encabezado o no empieza con "Bearer ", lo dejamos pasar (Spring Security decidirá si lo bloquea más adelante)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraemos el token (quitando los primeros 7 caracteres de "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // 3. Extraemos el correo del token
            final String userEmail = jwtService.extractEmail(jwt);

            // 4. Si hay correo y el usuario aún no está autenticado en este hilo
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 5. Validamos matemáticamente que el token sea correcto y no esté expirado
                if (jwtService.isValid(jwt, userDetails)) {
                    // Creamos la credencial de acceso oficial de Spring
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Le decimos a Spring: "Este usuario es legítimo, déjalo pasar"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 6. Continuamos con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}