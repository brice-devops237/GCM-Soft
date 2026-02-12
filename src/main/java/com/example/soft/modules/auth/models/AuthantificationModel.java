package com.example.soft.modules.auth.models;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité JPA représentant un utilisateur authentifié du système.
 *
 * <p>
 * Mappée sur la table {@code authantification}. Contient :
 * <ul>
 *     <li>Identité : nom, prénom, login, mot de passe (hashé), CNI, téléphone, sexe</li>
 *     <li>Profil : type, matricule, service</li>
 *     <li>Droits laboratoire : prélèvement, saisie, impression, validation, paramètres, stock, observation</li>
 *     <li>Droits caisse : réduction, duplicata, caution, pharmacie, encaissement, décaissement, etc.</li>
 *     <li>Droits stock, consultation, tableau de bord, gestion utilisateurs/corbeille, etc.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Les champs de type {@code int} (0/1) représentent des autorisations (0 = non, 1 = oui).
 * Le mot de passe doit être stocké hashé (BCrypt) côté service.
 * </p>
 */
@Entity
@Table(name = "authantification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthantificationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    private String nom;

    @Column(nullable = false, length = 30)
    private String prenom;

    /** Identifiant de connexion (unique). */
    @Column(nullable = false, length = 30, unique = true)
    private String login;

    /** Mot de passe hashé (BCrypt recommandé). */
    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 30)
    private String cni;

    @Column(nullable = false)
    private Integer tel;

    @Column(length = 30)
    @Builder.Default
    private String sexe = "inconue";

    @Column(nullable = false, length = 200)
    private String type;

    @Column(nullable = false, length = 50)
    private String matricule;

    @Column(nullable = false)
    private Integer service;

    /* ========== Laboratoire ========== */
    @Column(name = "labo_prelevement")
    @Builder.Default
    private Integer laboPrelevement = 1;
    @Column(name = "labo_saisie")
    @Builder.Default
    private Integer laboSaisie = 1;
    @Column(name = "labo_impression")
    @Builder.Default
    private Integer laboImpression = 1;
    @Column(name = "labo_validation")
    @Builder.Default
    private Integer laboValidation = 0;
    @Column(name = "labo_parametre")
    @Builder.Default
    private Integer laboParametre = 0;
    @Column(name = "labo_stock")
    @Builder.Default
    private Integer laboStock = 0;
    @Column(name = "labo_observation")
    @Builder.Default
    private Integer laboObservation = 0;

    /* ========== Caisse ========== */
    @Column(name = "caisse_reduction")
    @Builder.Default
    private Integer caisseReduction = 1;
    @Column(name = "caisse_duplicata")
    @Builder.Default
    private Integer caisseDuplicata = 1;
    @Column(name = "caisse_enregistrement_caution")
    @Builder.Default
    private Integer caisseEnregistrementCaution = 1;
    @Column(name = "caisse_paiment_caution")
    @Builder.Default
    private Integer caissePaimentCaution = 1;
    @Column(name = "caisse_remboursement_caution")
    @Builder.Default
    private Integer caisseRemboursementCaution = 1;
    @Column(name = "caisse_pharmacie")
    @Builder.Default
    private Integer caissePharmacie = 1;
    @Column(name = "caisse_encaissement")
    @Builder.Default
    private Integer caisseEncaissement = 1;
    @Column(name = "caisse_decaissement")
    @Builder.Default
    private Integer caisseDecaissement = 1;
    @Column(name = "caisse_recu_compensation")
    @Builder.Default
    private Integer caisseRecuCompensation = 0;

    @Column(name = "creer_patient")
    @Builder.Default
    private Integer creerPatient = 1;

    /* ========== Stock ========== */
    @Column(name = "stock_new_article")
    @Builder.Default
    private Integer stockNewArticle = 1;
    @Column(name = "stock_delete_article")
    @Builder.Default
    private Integer stockDeleteArticle = 1;
    @Column(name = "stock_edite_article")
    @Builder.Default
    private Integer stockEditeArticle = 1;
    @Column(name = "stock_ajust_article")
    @Builder.Default
    private Integer stockAjustArticle = 1;
    @Column(name = "stock_inventaire")
    @Builder.Default
    private Integer stockInventaire = 1;
    @Column(name = "stock_bc")
    @Builder.Default
    private Integer stockBc = 1;
    @Column(name = "stock_bl")
    @Builder.Default
    private Integer stockBl = 1;
    @Column(name = "stock_vente")
    @Builder.Default
    private Integer stockVente = 1;
    @Column(name = "stock_sortie_article")
    @Builder.Default
    private Integer stockSortieArticle = 1;
    @Column(name = "stock_approvisionnement")
    @Builder.Default
    private Integer stockApprovisionnement = 1;
    @Column(name = "stock_kit")
    @Builder.Default
    private Integer stockKit = 1;
    @Column(name = "stock_historique_vente")
    @Builder.Default
    private Integer stockHistoriqueVente = 1;

    /* ========== Consultation ========== */
    @Column(name = "consultation_service")
    @Builder.Default
    private Integer consultationService = 1;
    @Column(name = "consultation_qualification")
    @Builder.Default
    private Integer consultationQualification = 1;
    @Column(name = "consultation_matricule", length = 50)
    private String consultationMatricule;

    @Column(name = "tableau_bord")
    @Builder.Default
    private Integer tableauBord = 1;
    @Column(name = "annuler_facture")
    @Builder.Default
    private Integer annulerFacture = 1;
    @Column(name = "change_prescripteur")
    @Builder.Default
    private Integer changePrescripteur = 1;
    @Column(name = "gestion_user")
    @Builder.Default
    private Integer gestionUser = 1;
    @Column(name = "gestion_corbeille")
    @Builder.Default
    private Integer gestionCorbeille = 1;
    @Column(name = "recette_pharmacie")
    @Builder.Default
    private Integer recettePharmacie = 0;
    @Column(name = "patient_assure")
    @Builder.Default
    private Integer patientAssure = 0;
    @Column(name = "annuler_caution")
    @Builder.Default
    private Integer annulerCaution = 0;
}
