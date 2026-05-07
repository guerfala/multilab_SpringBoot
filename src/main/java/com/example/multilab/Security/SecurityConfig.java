package com.example.multilab.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════
 * SecurityConfig — Configuration Spring Security
 *
 * - BCrypt pour le hachage des mots de passe
 * - JWT pour l'authentification stateless
 * - CORS restreint (plus de origins = "*")
 * - Endpoints publics : login uniquement
 * - Endpoints admin : protégés par ROLE_ADMIN
 * ═══════════════════════════════════════════════════════════
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF (API stateless)
            .csrf(csrf -> csrf.disable())

            // CORS configuré proprement
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Session stateless (JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Règles d'autorisation
            .authorizeHttpRequests(auth -> auth
                // ── Endpoints publics ──
                .requestMatchers("/api/auth/login").permitAll()

                // ── Lecture ouverte à tous les authentifiés ──
                // (les commerciaux ont besoin de lire organismes et objets)
                .requestMatchers(HttpMethod.GET, "/api/organismes/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/objets/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/auth/users").authenticated()

                // ── Écriture admin uniquement ──
                .requestMatchers(HttpMethod.POST, "/api/organismes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/organismes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/organismes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/objets/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/objets/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/objets/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/addUser").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/auth/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/auth/{id}").hasRole("ADMIN")
                .requestMatchers("/api/ordres/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/missions").hasRole("ADMIN")

                // ── Tous les autres nécessitent authentification ──
                .anyRequest().authenticated()
            )

            // Ajouter le filtre JWT avant le filtre par défaut
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS restreinte
     * ⚠️ Adapter les origines selon votre environnement
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées (adapter selon l'environnement)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",      // Angular dev
            "http://localhost:3000",      // React dev
            "http://10.0.2.2:8080",       // Émulateur Android
            "https://multilab-tunisia.com.tn",    // ← AJOUTER
            "http://multilab-tunisia.com.tn"      // ← AJOUTER
        ));

        // Méthodes autorisées
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With"));

        // Exposer le header Authorization dans les réponses
        configuration.setExposedHeaders(List.of("Authorization"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
