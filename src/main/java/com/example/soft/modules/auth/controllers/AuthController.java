package com.example.soft.modules.auth.controllers;

import com.example.soft.modules.auth.config.JwtProperties;
import com.example.soft.modules.auth.dtos.LoginRequest;
import com.example.soft.modules.auth.dtos.LoginResponse;
import com.example.soft.modules.auth.dtos.UserInfoDto;
import com.example.soft.modules.auth.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur d'authentification : connexion (formulaire + API), profil utilisateur, déconnexion.
 *
 * <p>
 * Endpoints :
 * <ul>
 *     <li>GET  /login : page de connexion (vue Thymeleaf)</li>
 *     <li>POST /auth/login : soumission formulaire → cookie JWT + redirection dashboard</li>
 *     <li>POST /api/auth/login : API JSON → retourne token + user</li>
 *     <li>GET  /api/auth/me : utilisateur connecté (token requis)</li>
 *     <li>POST /auth/logout : suppression du cookie token</li>
 * </ul>
 * </p>
 */
@Controller
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    /**
     * Soumission du formulaire de connexion (page /login).
     * En cas de succès : écrit le token en cookie et redirige vers /dashboard.
     * En cas d'échec : redirige vers /login avec un message d'erreur.
     */
    @PostMapping("/auth/login")
    public String loginForm(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        LoginResponse loginResponse = authService.login(username, password);
        if (loginResponse == null) {
            redirectAttributes.addFlashAttribute("error", "Login ou mot de passe incorrect.");
            return "redirect:/login";
        }
        addTokenCookie(response, loginResponse.getToken());
        redirectAttributes.addFlashAttribute("success", "Connexion réussie.");
        return "redirect:/dashboard";
    }

    /**
     * API de connexion : attend un JSON { "username": "...", "password": "..." }
     * et retourne { "token", "type", "expiresInSeconds", "user" }.
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> loginApi(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        if (loginResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Retourne les informations de l'utilisateur connecté (token dans header ou cookie).
     */
    @GetMapping("/api/auth/me")
    @ResponseBody
    public ResponseEntity<UserInfoDto> me(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserInfoDto user = authService.getCurrentUserInfoByLogin(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Déconnexion : supprime le cookie contenant le token.
     */
    @PostMapping("/auth/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        clearTokenCookie(response);
        redirectAttributes.addFlashAttribute("success", "Vous avez été déconnecté.");
        return "redirect:/login";
    }

    @GetMapping("/auth/logout")
    public String logoutGet(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        clearTokenCookie(response);
        redirectAttributes.addFlashAttribute("success", "Vous avez été déconnecté.");
        return "redirect:/login";
    }

    private void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(jwtProperties.getCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtProperties.getExpirationMs() / 1000));
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    private void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtProperties.getCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
