package com.example.soft.modules.auth.repositories;

import com.example.soft.modules.auth.models.AuthantificationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour la table {@code authantification}.
 *
 * <p>
 * Fournit l'accès CRUD et les requêtes métier pour les utilisateurs authentifiés.
 * Utilisé par le service d'authentification et par le {@code UserDetailsService}.
 * </p>
 *
 * @see com.example.soft.modules.auth.services.AuthantificationService
 * @see com.example.soft.modules.auth.security.CustomUserDetailsService
 */
@Repository
public interface AuthantificationRepository extends JpaRepository<AuthantificationModel, Integer> {

    /**
     * Recherche un utilisateur par son login (identifiant de connexion).
     *
     * @param login identifiant unique de connexion
     * @return l'utilisateur s'il existe
     */
    Optional<AuthantificationModel> findByLogin(String login);

    /**
     * Vérifie l'existence d'un utilisateur avec le login donné.
     *
     * @param login identifiant de connexion
     * @return true si un enregistrement existe
     */
    boolean existsByLogin(String login);

    /**
     * Recherche par matricule.
     *
     * @param matricule matricule de l'utilisateur
     * @return l'utilisateur s'il existe
     */
    Optional<AuthantificationModel> findByMatricule(String matricule);

    /**
     * Recherche par type de profil.
     *
     * @param type type d'utilisateur
     * @return liste des utilisateurs de ce type
     */
    java.util.List<AuthantificationModel> findByType(String type);

    /**
     * Recherche par service.
     *
     * @param service identifiant du service
     * @return liste des utilisateurs du service
     */
    java.util.List<AuthantificationModel> findByService(Integer service);
}
