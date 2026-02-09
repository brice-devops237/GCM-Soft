package com.example.soft.modules.paramatre_patients.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entité JPA représentant les paramètres cliniques d'un patient.
 *
 * <p>
 * Chaque enregistrement correspond à une prise de paramètres
 * (signes vitaux, constantes biologiques, mensurations, etc.).
 * </p>
 *
 * <p>
 * Aucun champ n'est strictement obligatoire côté base,
 * sauf le code patient et la source.
 * </p>
 */
@Entity
@Table(name = "tb_parametres_patient")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametresPatientModel {

    /**
     * Identifiant technique unique.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Code unique du patient.
     * Doit exister dans la table patients.
     */
    @Column(name = "code_patient", nullable = false, length = 30)
    private String codePatient;

    /**
     * Code du traitement associé (optionnel).
     */
    @Column(name = "code_traitement", length = 100)
    private String codeTraitement;

    /**
     * Date de prise des paramètres.
     */
    private LocalDate date;

    /**
     * Heure de la prise (format libre).
     */
    private String heure;

    /* ===== Signes vitaux ===== */
    private Float ta;
    private Float fc;
    private Float fr;

    private String temp;
    private String sa02;

    /* ===== Données anthropométriques ===== */
    private String poids;
    private String taille;
    private String imc;

    /* ===== Autres paramètres ===== */
    private LocalDate ddr;
    private Integer pad;
    private Integer pas;
    private Float duirese;
    private Integer glycemie;
    private String pb;
    private String pc;

    /**
     * Source de la saisie (consultation, urgence, hospitalisation…).
     */
    @Column(nullable = false)
    private String source;

    /**
     * Informations complémentaires.
     */
    private String autre;
}
