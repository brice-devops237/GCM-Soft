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
        String token = extractToken(request);
        if (StringUtils.hasText(token)) {
            try {
                if (jwtService.validateToken(token)) {
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
                } else {
                    // Token invalide ou expiré : déconnexion immédiate sans sommation
                    clearTokenCookieAndRedirectIfNeeded(request, response);
                    return;
                }
            } catch (Exception e) {
                log.trace("JWT invalide ou expiré: {}", e.getMessage());
                clearTokenCookieAndRedirectIfNeeded(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Supprime le cookie JWT et redirige vers /login pour les requêtes navigateur.
     * Pour les requêtes API (Accept: application/json ou /api/*), envoie 401 sans redirection.
     */
    private void clearTokenCookieAndRedirectIfNeeded(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(jwtProperties.getCookieName(), "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        boolean isApiRequest = request.getRequestURI() != null && request.getRequestURI().startsWith(request.getContextPath() + "/api/");
        String accept = request.getHeader("Accept");
        boolean wantsJson = accept != null && accept.contains("application/json");

        if (!isApiRequest && !wantsJson) {
            response.sendRedirect(request.getContextPath() + "/login?session=expired");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token invalide ou expiré\"}");
        }
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
