package com.backend.huertohogar.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Si el token es inválido o expiró, la excepción se atrapa aquí.
                logger.error("Token inválido o expirado. Error: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 3. Cargar los detalles del usuario desde la DB (para obtener la contraseña cifrada y roles)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Validar el token (firma y expiración)
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 5. Crear el objeto de autenticación
                // Se usa userDetails.getAuthorities() que DEBE devolver "ROLE_ADMIN".
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Establecer el usuario en el contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continuar la cadena de filtros.
        // Si la autenticación no se estableció, Spring Security manejará la denegación (401/403).
        filterChain.doFilter(request, response);
    }
}
