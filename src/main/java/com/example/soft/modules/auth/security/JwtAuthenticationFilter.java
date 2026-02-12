package com.example.soft.modules.auth.security;

import com.example.soft.modules.auth.config.JwtProperties;
import com.example.soft.modules.auth.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre HTTP qui intercepte chaque requête pour authentifier l'utilisateur via JWT.
 *
 * <p>
 * Extrait le token depuis :
 * <ul>
 *     <li>L'en-tête {@code Authorization: Bearer &lt;token&gt;}</li>
 *     <li>Le cookie nommé par {@link JwtProperties#getCookieName()}</li>
 * </ul>
 * Si le token est valide, charge l'utilisateur et place l'authentification dans le SecurityContext.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (StringUtils.hasText(token) && jwtService.validateToken(token)) {
                String login = jwtService.getLoginFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(login);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.trace("JWT invalide ou expiré: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(jwtProperties.getHeaderName());
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7).trim();
        }
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (jwtProperties.getCookieName().equals(cookie.getName())) {
                    String v = cookie.getValue();
                    return StringUtils.hasText(v) ? v.trim() : null;
                }
            }
        }
        return null;
    }
}
