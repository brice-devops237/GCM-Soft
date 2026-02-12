package com.example.soft.modules.auth.dtos;

import com.example.soft.modules.auth.models.AuthantificationModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de représentation d'un utilisateur sans mot de passe (pour réponses API et profil).
 *
 * <p>
 * Utilisé pour {@code GET /api/auth/me} et toute réponse exposant les infos utilisateur
 * sans exposer le hash du mot de passe.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthantificationDto {

    private Integer id;
    private String nom;
    private String prenom;
    private String login;
    private String cni;
    private Integer tel;
    private String sexe;
    private String type;
    private String matricule;
    private Integer service;
    private String consultationMatricule;

    /** Crée un DTO à partir de l'entité (sans le mot de passe). */
    public static AuthantificationDto fromEntity(AuthantificationModel u) {
        if (u == null) return null;
        return AuthantificationDto.builder()
                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .login(u.getLogin())
                .cni(u.getCni())
                .tel(u.getTel())
                .sexe(u.getSexe())
                .type(u.getType())
                .matricule(u.getMatricule())
                .service(u.getService())
                .consultationMatricule(u.getConsultationMatricule())
                .build();
    }
}
