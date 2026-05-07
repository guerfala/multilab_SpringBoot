package com.example.multilab.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * ═══════════════════════════════════════════════════════════
 * JwtFilter — Filtre d'authentification JWT
 *
 * Intercepte chaque requête HTTP :
 * 1. Extrait le token du header "Authorization: Bearer xxx"
 * 2. Valide le token
 * 3. Injecte l'authentification dans le SecurityContext
 *
 * Les endpoints /api/auth/login et /api/auth/addUser sont exclus
 * ═══════════════════════════════════════════════════════════
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                // Créer l'authentification avec le rôle
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                        )
                    );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Exclure les endpoints publics du filtre JWT
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/auth/login");
    }
}
