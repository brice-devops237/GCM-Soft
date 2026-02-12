package com.example.soft.modules.auth.services;

import com.example.soft.modules.auth.models.AuthantificationModel;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des utilisateurs authentifiés (table {@code authantification}).
 *
 * <p>
 * Responsabilités :
 * <ul>
 *     <li>CRUD utilisateurs</li>
 *     <li>Recherche par login, matricule, type, service</li>
 *     <li>Vérification des identifiants (login + mot de passe) pour la connexion</li>
 * </ul>
 * </p>
 *
 * <p>
 * Le hashage des mots de passe (BCrypt) et la génération des tokens JWT sont délégués
 * au service d'authentification (AuthService) et au JwtService.
 * </p>
 *
 * @see com.example.soft.modules.auth.services.impl.AuthantificationServiceImpl
 * @see com.example.soft.modules.auth.services.AuthService
 */
public interface AuthantificationService {

    /**
     * Crée un nouvel utilisateur. Le mot de passe doit être hashé avant l'appel.
     *
     * @param user entité à persister
     * @return l'utilisateur créé (avec id généré)
     */
    AuthantificationModel create(AuthantificationModel user);

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id   identifiant de l'utilisateur
     * @param user données à appliquer (l'id et le password peuvent être exclus selon la logique métier)
     * @return l'utilisateur mis à jour
     */
    AuthantificationModel update(Integer id, AuthantificationModel user);

    /**
     * Récupère un utilisateur par son id.
     *
     * @param id identifiant
     * @return l'utilisateur trouvé
     * @throws jakarta.persistence.EntityNotFoundException si non trouvé
     */
    AuthantificationModel getById(Integer id);

    /**
     * Récupère un utilisateur par son login.
     *
     * @param login identifiant de connexion
     * @return l'utilisateur s'il existe
     */
    Optional<AuthantificationModel> getByLogin(String login);

    /**
     * Liste tous les utilisateurs.
     *
     * @return liste de tous les enregistrements
     */
    List<AuthantificationModel> getAll();

    /**
     * Supprime un utilisateur par id.
     *
     * @param id identifiant de l'utilisateur à supprimer
     */
    void deleteById(Integer id);

    /**
     * Vérifie si le mot de passe fourni correspond à celui de l'utilisateur (hash comparé).
     * Dépend de l'implémentation (BCrypt).
     *
     * @param user     utilisateur chargé depuis la base
     * @param rawPassword mot de passe en clair saisi
     * @return true si le mot de passe est valide
     */
    boolean matchesPassword(AuthantificationModel user, String rawPassword);
}
