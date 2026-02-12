package com.example.soft.modules.auth.services;

import com.example.soft.modules.auth.dtos.LoginRequest;
import com.example.soft.modules.auth.dtos.LoginResponse;
import com.example.soft.modules.auth.dtos.UserInfoDto;
import com.example.soft.modules.auth.models.AuthantificationModel;

/**
 * Service d'authentification : connexion, génération de token, récupération du profil.
 *
 * <p>
 * Utilise {@link AuthantificationService} pour charger l'utilisateur et vérifier le mot de passe,
 * et {@link JwtService} pour générer le token JWT.
 * </p>
 */
public interface AuthService {

    /**
     * Authentifie un utilisateur et retourne un token JWT avec les infos utilisateur.
     *
     * @param request login + mot de passe
     * @return LoginResponse avec token et user, ou null si échec
     */
    LoginResponse login(LoginRequest request);

    /**
     * Authentifie par login et mot de passe (convenience).
     *
     * @param username login
     * @param password mot de passe en clair
     * @return LoginResponse ou null
     */
    LoginResponse login(String username, String password);

    /**
     * Construit le DTO UserInfo à partir de l'entité (sans mot de passe).
     *
     * @param user entité chargée depuis la base
     * @return DTO pour l'API
     */
    UserInfoDto toUserInfo(AuthantificationModel user);

    /**
     * Récupère l'utilisateur par id (pour /me après validation du token).
     *
     * @param id identifiant
     * @return UserInfoDto ou null
     */
    UserInfoDto getCurrentUserInfo(Integer id);

    /**
     * Récupère les infos utilisateur par login (pour /me via SecurityContext).
     *
     * @param login login de l'utilisateur connecté
     * @return UserInfoDto ou null
     */
    UserInfoDto getCurrentUserInfoByLogin(String login);
}
