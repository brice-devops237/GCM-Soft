package com.example.soft.config;

import com.example.soft.modules.auth.dtos.UserInfoDto;
import com.example.soft.modules.auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Ajoute le nom et le rôle de l'utilisateur connecté au modèle pour toutes les vues.
 * Ajoute aussi le chemin de la requête (servletPath) pour le menu actif dans l'aside.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserModelAdvice {

    private final AuthService authService;

    @ModelAttribute
    public void addCurrentUser(Model model, HttpServletRequest request) {
        if (request != null) {
            model.addAttribute("servletPath", request.getServletPath() != null ? request.getServletPath() : "");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return;
        }
        String login = auth.getName();
        UserInfoDto user = authService.getCurrentUserInfoByLogin(login);
        if (user != null) {
            model.addAttribute("username", user.getLogin() != null && !user.getLogin().isBlank() ? user.getLogin() : "Utilisateur");
            model.addAttribute("role", user.getType() != null && !user.getType().isBlank() ? user.getType() : "Utilisateur");
        }
    }
}
