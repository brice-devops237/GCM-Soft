package com.example.soft.config;

import com.example.soft.modules.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration Spring Security : authentification par JWT, pas de session serveur.
 *
 * <p>
 * - Routes publiques (sans token) : /, /login, /api/auth/login, /assets/**, /error, /favicon.ico.
 * - Toutes les autres routes (y compris /dashboard, /patients, /api/** sauf /api/auth/login)
 *   nécessitent un token JWT valide dans l'en-tête {@code Authorization: Bearer <token>}.
 * </p>
 *
 * <p>
 * Le filtre {@link JwtAuthenticationFilter} est exécuté avant
 * {@link UsernamePasswordAuthenticationFilter} et remplit le SecurityContext à partir du token.
 * </p>
 *
 * <p>
 * Désactivation de la protection CSRF pour les API REST (stateless). Si des formulaires
 * Thymeleaf sont soumis en POST sans token, il faudra soit exclure ces URLs soit gérer CSRF.
 * Ici on garde stateless pour l'API et les pages Thymeleaf peuvent être protégées par le JWT
 * envoyé en header ou en cookie selon le front.
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final com.example.soft.config.JsonOrRedirectAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/login.html", "/logout", "/auth/login", "/auth/logout").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/logout").permitAll()
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/img/**", "/error", "/favicon.ico", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .headers(headers -> headers
                        .addHeaderWriter((request, response) -> {
                            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                            response.setHeader("Pragma", "no-cache");
                            response.setDateHeader("Expires", 0);
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
