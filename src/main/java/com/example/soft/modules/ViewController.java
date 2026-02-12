package com.example.soft.modules;

import com.example.soft.modules.auth.config.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur simple pour rediriger vers des pages statiques de vues.
 * Gère l'affichage des pages login, dashboard et la déconnexion (suppression du cookie JWT).
 */
@Controller
@RequiredArgsConstructor
public class ViewController {

    private final JwtProperties jwtProperties;

    /**
     * Affiche la page de connexion par défaut.
     *
     * @return chemin Thymeleaf vers la page login
     */
    @GetMapping("/login")
    public String loginPage() {
        return "pages/authentification/login";
    }

    /**
     * Affiche la page dashboard.
     *
     * @return chemin Thymeleaf vers la page dashboard
     */
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "pages/dashboard";
    }

    /**
     * Redirection profil vers le dashboard (page profil à compléter selon besoin).
     */
    @GetMapping("/profile")
    public String profileRedirect() {
        return "redirect:/dashboard";
    }

    /**
     * Redirection paramètres vers le dashboard (page paramètres à compléter selon besoin).
     */
    @GetMapping("/settings")
    public String settingsRedirect() {
        return "redirect:/dashboard";
    }

    /**
     * Redirection de la racine vers la page de login.
     */
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/login";
    }

    /**
     * Déconnexion : supprime le cookie JWT et redirige vers la page de login.
     *
     * @param response pour effacer le cookie auth_token
     * @return redirection vers /login
     */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtProperties.getCookieName(), "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }
}
