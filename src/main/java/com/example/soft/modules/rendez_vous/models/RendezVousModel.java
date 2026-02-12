package com.example.soft.modules.rendez_vous.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité JPA pour les rendez-vous patients.
 * Table : tb_rendez_vous_patient
 * Statut : 0 = En attente du patient, 1 = En attente du traitant, 2 = Rendez-vous honoré.
 */
@Entity
@Table(name = "tb_rendez_vous_patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Code patient (référence accueil.code) */
    @Column(nullable = false, length = 200)
    private String code;

    @Column(name = "code_consultation", length = 50)
    private String codeConsultation;

    /** Date du jour d'enregistrement */
    @Column(name = "date_jour", nullable = false)
    private LocalDate dateJour;

    @Column(name = "date_rendez_vous", nullable = false)
    private LocalDate dateRendezVous;

    @Column(name = "heure_rendez_vous", nullable = false)
    private LocalTime heureRendezVous;

    /** Médecin / traitant */
    @Column(name = "mat_traitent", nullable = false, length = 100)
    private String matTraitent;

    @Column(nullable = false, length = 500)
    private String objet;

    @Column(nullable = false, length = 50)
    private String user;

    /** 0 = En attente patient, 1 = En attente traitant, 2 = Honoré */
    @Column(nullable = false)
    @Builder.Default
    private Integer statut = 0;

    public static final int STATUT_ATTENTE_PATIENT = 0;
    public static final int STATUT_ATTENTE_TRAITANT = 1;
    public static final int STATUT_HONORE = 2;
}
