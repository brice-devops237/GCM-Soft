package com.example.soft.modules;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur simple pour rediriger vers des pages statiques de vues.
 * Ici, il gère l'affichage des pages login et dashboard.
 */
@Controller
public class ViewController {

    /**
     * Affiche la page de connexion par défaut.
     *
     * @return chemin Thymeleaf vers la page login
     */
    @GetMapping("/login")
    public String loginPage() {
        // Thymeleaf recherchera le fichier src/main/resources/templates/pages/authentification/login.html
        return "pages/authentification/login";
    }

    /**
     * Affiche la page dashboard.
     *
     * @return chemin Thymeleaf vers la page dashboard
     */
    @GetMapping("/dashboard")
    public String dashboardPage() {
        // Thymeleaf recherchera le fichier src/main/resources/templates/pages/dashboard/dashboard.html
        return "pages/dashboard";
    }

    /**
     * Redirection de la racine vers la page de login.
     */
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/login";
    }
}
