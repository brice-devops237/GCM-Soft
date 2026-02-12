package com.example.soft.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Point d'entrée en cas d'accès non authentifié : redirection vers /login pour les requêtes
 * navigateur (HTML), et réponse 401 JSON pour les requêtes API (/api/** ou Accept: application/json).
 */
@Component
public class JsonOrRedirectAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        boolean isApi = request.getRequestURI() != null && request.getRequestURI().startsWith(request.getContextPath() + "/api/")
                || isAcceptJson(request);

        if (isApi) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Non authentifié\"}");
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    private boolean isAcceptJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }
}
