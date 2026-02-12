package com.example.soft.modules.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO exposant les informations publiques d'un utilisateur authentifié.
 *
 * <p>
 * Utilisé dans la réponse de login et pour l'endpoint "me".
 * Ne contient jamais le mot de passe ni de données sensibles.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

    private Integer id;
    private String nom;
    private String prenom;
    private String login;
    private String type;
    private String matricule;
    private Integer service;
    private String sexe;
}
