package com.example.soft.modules.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse après connexion réussie (API JSON).
 *
 * <p>
 * Contient le token JWT, les informations de l'utilisateur connecté
 * et la durée de validité du token en secondes.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /** Token JWT à envoyer dans l'en-tête Authorization ou en cookie. */
    private String token;

    /** Type du token (ex: "Bearer"). */
    @Builder.Default
    private String type = "Bearer";

    /** Durée de validité du token en secondes. */
    private long expiresInSeconds;

    /** Informations publiques de l'utilisateur (sans mot de passe). */
    private UserInfoDto user;
}
