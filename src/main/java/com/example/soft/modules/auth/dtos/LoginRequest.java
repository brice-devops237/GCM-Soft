package com.example.soft.modules.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour la connexion (formulaire ou API).
 *
 * <p>
 * Champs attendus : {@code username} (login) et {@code password}.
 * Le nom {@code username} est utilisé pour rester compatible avec le formulaire
 * HTML et avec Spring Security (paramètre par défaut).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /** Identifiant de connexion (login de la table authantification). */
    @NotBlank(message = "Le login est obligatoire")
    private String username;

    /** Mot de passe en clair (sera vérifié côté serveur). */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
