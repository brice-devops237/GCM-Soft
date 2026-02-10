package com.example.soft.modules.patients.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * Représente un patient dans le système.
 *
 * <p>
 * Entité JPA mappée sur la table SQL 'accueil'. Contient :
 * <ul>
 *     <li>Informations personnelles : nom, sexe, date de naissance, contact, etc.</li>
 *     <li>Informations administratives : code, statut, type, catégorie, assurance, etc.</li>
 *     <li>Informations médicales ou complémentaires : groupe sanguin, urgence, image, etc.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Bonnes pratiques :
 * <ul>
 *     <li>Utilisation de Lombok pour générer getters/setters, constructeur, builder, etc.</li>
 *     <li>Gestion des valeurs par défaut via @Builder.Default.</li>
 *     <li>Protection des champs critiques (code patient, dateCreation) contre les modifications.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "accueil")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Code unique généré automatiquement par le système (ex: 23PA1) */
    @Column(nullable = false, length = 30, unique = true)
    private String code;

    @Builder.Default
    @Column(name = "code_formation", length = 100)
    private String codeFormation = "F1095/236";

    @Column(nullable = false, length = 200)
    private String noms;

    @Column(nullable = false, length = 30)
    private String sexe;

    @Builder.Default
    @Column(name = "lateralite_dominante")
    private Integer lateraliteDominante = 0;

    @Builder.Default
    @Column(length = 30)
    private String age = "0";

    @Builder.Default
    @Column(length = 30)
    private String secteur = "-Selectionner-";

    @Column(nullable = false, length = 30)
    private String consulteur;

    @Column(nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(length = 30)
    private String phone = "Inconnu";

    @Column(length = 30)
    private String cni;

    @Column(name = "print_by", nullable = false, length = 30)
    private String printBy;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Builder.Default
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(length = 10)
    private String groupe;

    @Column(length = 100)
    private String adresse;

    @Column(length = 50)
    private String ville;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Builder.Default
    @Column(length = 30)
    private String statut = "non cloture";

    @Column(length = 200)
    private String profession;

    @Builder.Default
    @Column(length = 100)
    private String lieuNaissance = "";

    @Builder.Default
    @Column(length = 100)
    private String nationalite = "";

    @Column(length = 200)
    private String ethnie;

    @Column(length = 200)
    private String religion;

    @Column(name = "etat_matrimonial", length = 200)
    private String etatMatrimonial;

    @Column(length = 200)
    private String urgence;

    /** Relation avec la personne à contacter (ex: Père, Mère, Autre). */
    @Column(name = "relation_urgence", length = 100)
    private String relationUrgence;

    @Column(name = "contact_urgence", length = 20)
    private String contactUrgence;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Builder.Default
    @Column(name = "type_patient", length = 20)
    private String typePatient = "Normal";

    @Builder.Default
    @Column(name = "premiere_fois_UPEC")
    private Boolean premiereFoisUPEC = false;

    @Column(name = "nom_mere", length = 200)
    private String nomMere;

    @Column(name = "nom_pere", length = 200)
    private String nomPere;

    @Builder.Default
    @Column(name = "plan_comptable")
    private Integer planComptable = 0;

    @Builder.Default
    @Column(name = "id_assurance")
    private Integer idAssurance = 0;

    @Column(length = 200)
    private String matricule;

    @Builder.Default
    private Integer categorie = 0;

    @Column(name = "id_type_armee")
    private Integer idTypeArmee;

    @Column(name = "id_grade_armee")
    private Integer idGradeArmee;

    @Column(name = "dossier_maitre_id")
    private Integer dossierMaitreId;

    @Builder.Default
    @Column(name = "est_fusionne")
    private Boolean estFusionne = false;

    @Column(name = "patient_ref_code", length = 50)
    private String patientRefCode;

    @Builder.Default
    @Column(name = "est_actif")
    private Boolean estActif = true;

    /** Vérifie si le patient est actif */
    public boolean isActive() {
        return Boolean.TRUE.equals(estActif);
    }
}
